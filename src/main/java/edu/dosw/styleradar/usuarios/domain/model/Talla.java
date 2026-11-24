package edu.dosw.styleradar.usuarios.domain.model;

public enum Talla {
    XS,
    S,
    M,
    L,
    XL,
    XXL;

    public static boolean esValidaPara(TipoPrenda tipoPrenda, String talla) {
        if (tipoPrenda == null || talla == null) {
            return false;
        }

        if (tipoPrenda == TipoPrenda.SUPERIOR) {
            try {
                Talla.valueOf(talla);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        try {
            int numero = Integer.parseInt(talla);

            if (tipoPrenda == TipoPrenda.INFERIOR) {
                return numero >= 26 && numero <= 36;
            }

            if (tipoPrenda == TipoPrenda.CALZADO) {
                return numero >= 34 && numero <= 45;
            }

            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

