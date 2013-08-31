package name.prokop.bart.gae.edziecko.util;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class PMF {
	private static final PersistenceManagerFactory instance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private PMF() {
	}

	public static PersistenceManagerFactory get() {
		return instance;
	}
	
	public static PersistenceManager getPM() {
		return get().getPersistenceManager();
	}
}
