package name.prokop.bart.gae.edziecko.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.*;
import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import name.prokop.bart.gae.edziecko.bol.*;
import name.prokop.bart.gae.edziecko.bol.cacheable.CacheableZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.mailer.ExceptionReportMail;
import name.prokop.bart.gae.edziecko.util.mailer.Mailer;

public enum IC {

    INSTANCE;
    private final Cache cache;
    private final Map<UUID, Object> objects = new HashMap<UUID, Object>();

    private IC() {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized void addObject(UUID uuid, Object object) {
        objects.put(uuid, object);
    }

    public synchronized Object getObject(UUID uuid) {
        return objects.get(uuid);
    }

    public synchronized void addCachedObject(String uuid, Object object) {
        cache.put(uuid, object);
    }

    public synchronized Object getCachedObject(String uuid) {
        return cache.get(uuid);
    }

    private void replace(RozliczenieMiesieczne rozliczenieMiesieczne) {
        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            RozliczenieMiesieczne rm = pm.getObjectById(RozliczenieMiesieczne.class, rozliczenieMiesieczne.getKey());
            rm.setBilansOtwarcia(rozliczenieMiesieczne.getBilansOtwarcia());
            rm.setParametry(rozliczenieMiesieczne.getParametry());
            rm.setWplaty(rozliczenieMiesieczne.getWplaty());
            rm.setZaliczki(rozliczenieMiesieczne.getZaliczki());
            rm.setZuzycie(rozliczenieMiesieczne.getZuzycie());
            pm.currentTransaction().commit();
            String mcKey = RozliczenieMiesieczne.buildID(rozliczenieMiesieczne.getPrzedszkoleKey(), rozliczenieMiesieczne.getRokMiesiac());
            cache.put(mcKey, rm);
        } finally {
            pm.close();
        }
    }

    public synchronized void replace(LogicEntity entity) {
        if (entity instanceof Przedszkole) {
            ((Przedszkole) entity).populateLocalCache();
        }
        if (entity instanceof RozliczenieMiesieczne) {
            replace((RozliczenieMiesieczne) entity);
        }
        cache.put(entity.getKey(), entity);
    }

    public synchronized Przedszkole findPrzedszkole(Long id) {
        Key key = KeyFactory.createKey("Przedszkole", id);
        return findPrzedszkole(key);
    }

    public synchronized Przedszkole findPrzedszkole(Key key) {
        if (cache.containsKey(key)) {
            Przedszkole przedszkole = (Przedszkole) cache.get(key);
            przedszkole.populateLocalCache();
            return przedszkole;
        }

        PersistenceManager pm = PMF.getPM();
        try {
            return findPrzedszkole(pm, key);
        } finally {
            pm.close();
        }
    }

    private Przedszkole findPrzedszkole(PersistenceManager pm, Key key) {
        Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, key);
        przedszkole.populateLocalCache();
        cache.put(key, przedszkole);
        return przedszkole;
    }

    public synchronized Karta getKartaByCN(String cn) {
        PersistenceManager pm = PMF.getPM();
        try {
            Karta karta = Karta.findByCN(pm, cn);
            if (karta != null) {
                return findPrzedszkole(pm, karta.getDziecko().getPrzedszkole().getKey()).getKarta(karta.getKey());
            }
            return null;
        } finally {
            pm.close();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("cache.getCacheStatistics().getObjectCount(): ").append(cache.getCacheStatistics().getObjectCount()).append("\n");
        sb.append("cache.getCacheStatistics().getCacheHits(): ").append(cache.getCacheStatistics().getCacheHits()).append("\n");
        sb.append("cache.getCacheStatistics().getCacheMisses(): ").append(cache.getCacheStatistics().getCacheMisses()).append("\n");
        return sb.toString();
    }

    public synchronized void clear() {
        cache.clear();
        cache.evict();
    }

    public synchronized RozliczenieMiesieczne retrieveRozliczenieMiesieczne(Key pKey, int okres) {
        String mcKey = RozliczenieMiesieczne.buildID(pKey, okres);
        RozliczenieMiesieczne retVal = (RozliczenieMiesieczne) cache.get(mcKey);
        if (retVal != null) {
            return retVal;
        }

        PersistenceManager pm = PMF.getPM();
        try {
            Query q = pm.newQuery(RozliczenieMiesieczne.class);
            q.setFilter("przedszkoleKey == przedszkoleKeyParam && rokMiesiac == rokMiesiacParam");
            q.declareParameters(Key.class.getName() + " przedszkoleKeyParam, " + "Integer rokMiesiacParam");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("przedszkoleKeyParam", pKey);
            params.put("rokMiesiacParam", okres);

            @SuppressWarnings("unchecked")
            List<RozliczenieMiesieczne> rozliczenia = (List<RozliczenieMiesieczne>) q.executeWithMap(params);
            if (rozliczenia.size() == 1) {
                retVal = rozliczenia.get(0);
                cache.put(mcKey, retVal);
                return retVal;
            } else {
                retVal = new RozliczenieMiesieczne();
                retVal.setPrzedszkoleKey(pKey);
                retVal.setRokMiesiac(okres);

                Przedszkole przedszkole = findPrzedszkole(pKey);
                for (Dziecko d : przedszkole.getDzieci()) {
                    retVal.getBilansOtwarcia().setOpieka(d.getKey().getId(), 0.0);
                    retVal.getBilansOtwarcia().setZywienie(d.getKey().getId(), 0.0);
                }
                retVal.persist(pm);
                cache.put(mcKey, retVal);
                return retVal;
            }
        } finally {
            pm.close();
        }
    }

    public synchronized CacheableZbiorZdarzen retrieveZbiorZdarzen(String key) {
        return (CacheableZbiorZdarzen) cache.get(key);
    }

    public synchronized void store(String key, ZbiorZdarzen zbiorZdarzen) {
        cache.put(key, new CacheableZbiorZdarzen(zbiorZdarzen));
    }

    public synchronized void store(String key, CacheableZbiorZdarzen zbiorZdarzen) {
        cache.put(key, zbiorZdarzen);
    }

    public synchronized void remove(String key) {
        cache.remove(key);
    }
}
