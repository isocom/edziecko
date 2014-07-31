/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import java.util.Date;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public enum XLSType {

    Dzieci {

        @Override
        XLSReport getRenderer() {
            return new Dzieci();
        }
    }, DumpPrzedszkole {

        @Override
        XLSReport getRenderer() {
            return new DumpPrzedszkole();
        }
    }, BO {

        @Override
        XLSReport getRenderer() {
            return new BO();
        }
    }, ListaObecnosci {

        @Override
        XLSReport getRenderer() {
            return new ListaObecnosci();
        }
    }, Raporty {

        @Override
        XLSReport getRenderer() {
            return new Raporty();
        }
    }, Rozliczenie {

        @Override
        XLSReport getRenderer() {
            return new Rozliczenie();
        }
    };

    abstract XLSReport getRenderer();

    String buildName(EDzieckoRequest request) {
        switch (this) {
            case DumpPrzedszkole:
            case Dzieci:
                return name() + ".xls";
            case BO:
            case Raporty:
            case Rozliczenie:
                return name() + "-" + request.getRokMiesiac() + ".xls";
            case ListaObecnosci:
                return name() + "-" + DateToolbox.getFormatedDate("yyyyMMdd", (Date) request.getSession().getAttribute("data")) + ".xls";
            default:
                return "Unknown.xls";
        }
    }

    public String buildLink() {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=XLS?type=").append(this.name()).append(">");
        sb.append("XLS");
        sb.append("</a>");
        return sb.toString();
    }
}
