package model;

public class ValidadorDocumento {

    public static boolean isCPFValido(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0, peso = 10;

            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig1 = 11 - (soma % 11);
            dig1 = (dig1 > 9) ? 0 : dig1;

            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig2 = 11 - (soma % 11);
            dig2 = (dig2 > 9) ? 0 : dig2;

            return dig1 == (cpf.charAt(9) - '0') &&
                   dig2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isCNPJValido(String cnpj) {
        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        try {
            int soma = 0;
            int peso = 2;

            for (int i = 11; i >= 0; i--) {
                soma += (cnpj.charAt(i) - '0') * peso;
                peso = (peso == 9) ? 2 : peso + 1;
            }

            int dig1 = soma % 11;
            dig1 = (dig1 < 2) ? 0 : 11 - dig1;

            soma = 0;
            peso = 2;

            for (int i = 12; i >= 0; i--) {
                soma += (cnpj.charAt(i) - '0') * peso;
                peso = (peso == 9) ? 2 : peso + 1;
            }

            int dig2 = soma % 11;
            dig2 = (dig2 < 2) ? 0 : 11 - dig2;

            return dig1 == (cnpj.charAt(12) - '0') &&
                   dig2 == (cnpj.charAt(13) - '0');

        } catch (Exception e) {
            return false;
        }
    }
}
