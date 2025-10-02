public class Main {
    public static void main(String[] args) {
        int iSatu, iDua, iTiga, iEmpat, iLima;

        iDua = 0;
        iTiga = 0;
        iEmpat = 0;
        iLima = 0;

        int perubahanDua = 0;
        String saatDuaBerubah = "";
        int perubahanTiga = 0;
        int iEmpatSaatTigaKedua = 0;
        String saatTigaNegatif = "";
        String nilaiTigaNegatif = "";

        for (iSatu = 0; iSatu <= 10; iSatu++) {
            int iDuaSebelum = iDua;
            int iTigaSebelum = iTiga;

            if ((iSatu % 4) == 0) {
                iDua = iDua + iSatu + iTiga - iLima;
                if (iDua != iDuaSebelum) {
                    perubahanDua++;
                    saatDuaBerubah += iSatu + ",";
                }
            }

            if ((iSatu % 3) == 0) {
                iTiga = iTiga + (iSatu + iDua) - iEmpat;
                if (iTiga != iTigaSebelum) {
                    perubahanTiga++;
                    if (perubahanTiga == 2) {
                        iEmpatSaatTigaKedua = iEmpat;
                    }
                }
            }

            if ((iSatu % 2) == 0) {
                iEmpat = iEmpat + (iSatu + iDua) - iTiga;
            }

            if ((iSatu % 1) == 0) {
                iLima = iSatu + iDua + iTiga;
            }

            // Cek jika iTiga < 0
            if (iTiga < 0) {
                saatTigaNegatif += iSatu + ",";
                nilaiTigaNegatif += iTiga + ",";
            }

            System.out.println("iSatu=" + iSatu +
                    " | iDua=" + iDua +
                    " | iTiga=" + iTiga +
                    " | iEmpat=" + iEmpat +
                    " | iLima=" + iLima);
        }

        // Output hasil
        System.out.println("\n=== HASIL AKHIR ===");
        System.out.println("1. Nilai akhir variabel:");
        System.out.println("a. iSatu = " + iSatu);
        System.out.println("b. iDua = " + iDua);
        System.out.println("c. iTiga = " + iTiga);
        System.out.println("d. iEmpat = " + iEmpat);
        System.out.println("e. iLima = " + iLima);

        System.out.println("\n2. Perubahan iDua:");
        System.out.println("a. Nilai iDua berubah: " + perubahanDua + " kali");
        System.out.println("b. Pada iSatu: " + saatDuaBerubah.substring(0, saatDuaBerubah.length()-1));

        System.out.println("\n3. Perubahan iTiga:");
        System.out.println("a. Nilai iTiga berubah: " + perubahanTiga + " kali");
        System.out.println("b. Nilai iEmpat pada perubahan kedua iTiga: " + iEmpatSaatTigaKedua);

        System.out.println("\n4. Nilai iTiga < 0:");
        System.out.println("a. iSatu: " + saatTigaNegatif.substring(0, saatTigaNegatif.length()-1));
        System.out.println("b. Nilai iTiga: " + nilaiTigaNegatif.substring(0, nilaiTigaNegatif.length()-1));
    }
}