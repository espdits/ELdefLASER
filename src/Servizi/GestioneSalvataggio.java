package Servizi;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.dellapenna.research.ldr.LineaDeformabile;
import org.dellapenna.research.ldr.Quadrato;

/*
 * Sarebbe da donare un tocco in più di dinamicità pensando ad un potenziale aumento della modifiche effetuate. 
 */
/**
 * Classe che permette il salvataggio di un file EXCEL dei dati ottenuti
 *
 * @author Esteban Lombardozzi
 */
public abstract class GestioneSalvataggio {

    public static void salvaDATA(HashMap<Integer,LineaDeformabile> popolazone, int contatoreMosse, String nome_file) throws IOException {

        // Salvo i valori che devo salvare in una struttura dati di facile gestione.
        int matr_valori[][] = new int[popolazone.size()][contatoreMosse];
        String matr_mod[][] = new String[popolazone.size()][contatoreMosse];
        double[] contFitness;
        contFitness = new double[popolazone.size()];
        //Stampa valori su schermo e riempe matrice di valori e arraylist val_fitness
        riempiMatr(matr_valori, matr_mod, popolazone, contFitness);

        Workbook wb = new HSSFWorkbook();
        //Creo foglio Excel 
        Sheet sh = wb.createSheet();

        //Stile cella alta Legenda
        CellStyle yellow = wb.createCellStyle();
        yellow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        yellow.setFillBackgroundColor(HSSFColor.YELLOW.index);
        yellow.setFillForegroundColor(HSSFColor.YELLOW.index);
        yellow.setBorderBottom(CellStyle.BORDER_THIN);
        yellow.setBorderLeft(CellStyle.BORDER_THIN);
        yellow.setBorderRight(CellStyle.BORDER_THIN);
        yellow.setBorderTop(CellStyle.BORDER_THIN);
        yellow.setAlignment(CellStyle.ALIGN_CENTER);

        //Stile cella per gli individui blu prima riga 
        CellStyle blue_ind = wb.createCellStyle();
        blue_ind.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        blue_ind.setFillBackgroundColor(HSSFColor.BLUE.index);
        blue_ind.setFillForegroundColor(HSSFColor.BLUE.index);
        blue_ind.setBorderRight(CellStyle.BORDER_THIN);
        blue_ind.setAlignment(CellStyle.ALIGN_CENTER);

        //Stile cella per gli individui blu riga 2
        CellStyle blue_ind2 = wb.createCellStyle();
        blue_ind2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        blue_ind2.setFillBackgroundColor(HSSFColor.BLUE.index);
        blue_ind2.setFillForegroundColor(HSSFColor.BLUE.index);
        blue_ind2.setBorderRight(CellStyle.BORDER_THIN);
        blue_ind2.setAlignment(CellStyle.ALIGN_CENTER);
        blue_ind2.setBorderBottom(CellStyle.BORDER_THIN);

        //Stile cella per gli individui verdi riga 1
        CellStyle green_ind = wb.createCellStyle();
        green_ind.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        green_ind.setFillBackgroundColor(HSSFColor.GREEN.index);
        green_ind.setFillForegroundColor(HSSFColor.GREEN.index);
        green_ind.setBorderRight(CellStyle.BORDER_THIN);
        green_ind.setAlignment(CellStyle.ALIGN_CENTER);

        //Stile cella per gli individui verdi riga 2
        CellStyle green_ind2 = wb.createCellStyle();
        green_ind2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        green_ind2.setFillBackgroundColor(HSSFColor.GREEN.index);
        green_ind2.setFillForegroundColor(HSSFColor.GREEN.index);
        green_ind2.setBorderRight(CellStyle.BORDER_THIN);
        green_ind2.setAlignment(CellStyle.ALIGN_CENTER);
        green_ind2.setBorderBottom(CellStyle.BORDER_THIN);

        //Stile cella per le posizioni blu
        CellStyle blue_pos = wb.createCellStyle();
        blue_pos.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        blue_pos.setFillBackgroundColor(HSSFColor.BLUE.index);
        blue_pos.setFillForegroundColor(HSSFColor.BLUE.index);
        blue_pos.setBorderRight(CellStyle.BORDER_THIN);
        blue_pos.setAlignment(CellStyle.ALIGN_CENTER);

        //Stile cella per le posizioni verdi
        CellStyle green_pos = wb.createCellStyle();
        green_pos.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        green_pos.setFillBackgroundColor(HSSFColor.GREEN.index);
        green_pos.setFillForegroundColor(HSSFColor.GREEN.index);
        green_pos.setBorderRight(CellStyle.BORDER_THIN);
        green_pos.setAlignment(CellStyle.ALIGN_CENTER);

        //Stile cella per le modifiche blu
        CellStyle blue_mod = wb.createCellStyle();
        blue_mod.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        blue_mod.setFillBackgroundColor(HSSFColor.BLUE.index);
        blue_mod.setFillForegroundColor(HSSFColor.BLUE.index);
        blue_mod.setBorderRight(CellStyle.BORDER_THIN);
        blue_mod.setAlignment(CellStyle.ALIGN_CENTER);
        blue_mod.setBorderBottom(CellStyle.BORDER_THIN);

        //Stile cella per le modifiche verdi
        CellStyle green_mod = wb.createCellStyle();
        green_mod.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        green_mod.setFillBackgroundColor(HSSFColor.GREEN.index);
        green_mod.setFillForegroundColor(HSSFColor.GREEN.index);
        green_mod.setBorderRight(CellStyle.BORDER_THIN);
        green_mod.setAlignment(CellStyle.ALIGN_CENTER);
        green_mod.setBorderBottom(CellStyle.BORDER_THIN);

        //Stile cella per valore di fitness
        CellStyle red = wb.createCellStyle();
        red.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        red.setFillBackgroundColor(HSSFColor.RED.index);
        red.setFillForegroundColor(HSSFColor.RED.index);
        red.setBorderRight(CellStyle.BORDER_THIN);
        red.setAlignment(CellStyle.ALIGN_CENTER);
        red.setBorderBottom(CellStyle.BORDER_THIN);
        red.setBorderTop(CellStyle.BORDER_THIN);
        
        //Stile cella per cella numero valore di fitness
        CellStyle pink = wb.createCellStyle();
        pink.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        pink.setFillBackgroundColor(HSSFColor.PINK.index);
        pink.setFillForegroundColor(HSSFColor.PINK.index);
        pink.setBorderRight(CellStyle.BORDER_THIN);
        pink.setAlignment(CellStyle.ALIGN_CENTER);
        pink.setBorderBottom(CellStyle.BORDER_THIN);
        pink.setBorderTop(CellStyle.BORDER_THIN);

        // indice individuo + supporto salvataggio prima riga POSIZIONE
        int individuo_index = 1;
        // supporto stampa valori + supporto salvataggio seconda riga MODIFICA
        int aux_valori_index = 0;

// genero una file excel dove ogni riga rappresenta l'indiviuo creato.
        //facciamo per 2 per una maggiore leggibilità dei dati nel file creato
        for (int numRighe = 0; numRighe <= (2 * popolazone.size()); numRighe++) {

            Row row = sh.createRow(numRighe);
            for (int cellnum = 0; cellnum < contatoreMosse + 2; cellnum++) {
                //Casella in alto a sx pos(0,0)
                if (numRighe == 0 && cellnum == 0) {
                    Cell cell = row.createCell(cellnum);
                    cell.setCellValue("Individui");
                    cell.setCellStyle(yellow);
                }
                //Etichette pos(0,n)
                if (numRighe == 0 && cellnum > 0) {
                    Cell cell = row.createCell(cellnum);
                    if (cellnum <= contatoreMosse) {
                        cell.setCellValue("POS/MOD");
                        cell.setCellStyle(yellow);
                    } else if (cellnum == contatoreMosse + 1) {
                        cell.setCellValue("Val. Fitness");
                        cell.setCellStyle(red);
                    }
                    /*  switch (cellnum) {
                        case 1:
                            cell.setCellValue("POS/MOD");
                            cell.setCellStyle(yellow);
                            break;
                        case 2:
                            cell.setCellValue("POS/MOD");
                            cell.setCellStyle(yellow);
                            break;
                        case 3:
                            cell.setCellValue("POS/MOD");
                            cell.setCellStyle(yellow);
                            break;
                        case 4:
                            cell.setCellValue("POS/MOD");
                            cell.setCellStyle(yellow);
                            break;
                        case 5:
                            cell.setCellValue("POS/MOD");
                            cell.setCellStyle(yellow);
                            break;
                        case 6:
                            cell.setCellValue("Val. Fitness");
                            cell.setCellStyle(red);
                            break;
                        default:
                            System.out.print("Errore cella file!");

                    }*/
                }


                // Celle contenenti il numero dell'individuo
                if (cellnum == 0 && numRighe > 0) {
                    if (numRighe % 2 != 0) {
                        Cell cell = row.createCell(cellnum);
                        cell.setCellValue(individuo_index);
                        //visualizzazione alternata colori
                        if (individuo_index % 2 != 0) {
                            cell.setCellStyle(blue_ind);
                        } else {
                            cell.setCellStyle(green_ind);
                        }
                        aux_valori_index++;

                    } else {
                        Cell cell = row.createCell(cellnum);
                        //visualizzazione alternata colori
                        if (individuo_index % 2 != 0) {
                            cell.setCellStyle(blue_ind2);
                        } else {
                            cell.setCellStyle(green_ind2);
                        }
                        individuo_index++;
                    }

                }

                //riempo di dati!!!!!!
                if (cellnum > 0 && numRighe > 0 && cellnum <= contatoreMosse) {
                    Cell cell = row.createCell(cellnum);
                    if (numRighe % 2 != 0) {
                        cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                        if (individuo_index % 2 != 0) {
                            cell.setCellStyle(blue_pos);
                        } else {
                            cell.setCellStyle(green_pos);
                        }

                    } else {
                        //Parte Tipo di modifica
                        String aux = matr_mod[aux_valori_index - 1][cellnum - 1];
                        cell.setCellValue(aux);
                        if (aux_valori_index % 2 != 0) {
                            cell.setCellStyle(blue_mod);
                        } else {
                            cell.setCellStyle(green_mod);
                        }

                    }
                    /*switch (cellnum) {
                        default:
                            //Dovrebbe funzionare soltanto con un caso. Quindi senza Switch case.
                            System.err.println("errore riempimento dati foglio excel VALORIs");
                        case 1:
                            if (numRighe % 2 != 0) {
                                cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                                if (individuo_index % 2 != 0) {
                                    cell.setCellStyle(blue_pos);
                                } else {
                                    cell.setCellStyle(green_pos);
                                }

                            } else {
                                //Parte Tipo di modifica
                                String aux = matr_mod[aux_valori_index - 1][cellnum - 1];
                                cell.setCellValue(aux);
                                if (aux_valori_index % 2 != 0) {
                                    cell.setCellStyle(blue_mod);
                                } else {
                                    cell.setCellStyle(green_mod);
                                }

                            }
                            break;

                        case 2:
                            if (numRighe % 2 != 0) {

                                cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                                if (individuo_index % 2 != 0) {
                                    cell.setCellStyle(blue_pos);
                                } else {
                                    cell.setCellStyle(green_pos);
                                }

                            } else {
                                //Parte Tipo di modifica

                                String aux = matr_mod[aux_valori_index - 1][cellnum - 1];
                                cell.setCellValue(aux);
                                if (aux_valori_index % 2 != 0) {
                                    cell.setCellStyle(blue_mod);
                                } else {
                                    cell.setCellStyle(green_mod);
                                }
                            }
                            break;

                        case 3:
                            if (numRighe % 2 != 0) {

                                cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                                if (individuo_index % 2 != 0) {
                                    cell.setCellStyle(blue_pos);
                                } else {
                                    cell.setCellStyle(green_pos);
                                }

                            } else {
                                //Parte Tipo di modifica

                                String aux = matr_mod[aux_valori_index - 1][cellnum - 1];
                                cell.setCellValue(aux);
                                if (aux_valori_index % 2 != 0) {
                                    cell.setCellStyle(blue_mod);
                                } else {
                                    cell.setCellStyle(green_mod);
                                }
                            }
                            break;

                        case 4:
                            if (numRighe % 2 != 0) {

                                cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                                if (individuo_index % 2 != 0) {
                                    cell.setCellStyle(blue_pos);
                                } else {
                                    cell.setCellStyle(green_pos);
                                }
                            } else {
                                //Parte Tipo di modifica

                                String aux = matr_mod[aux_valori_index - 1][cellnum - 1];
                                cell.setCellValue(aux);
                                if (aux_valori_index % 2 != 0) {
                                    cell.setCellStyle(blue_mod);
                                } else {
                                    cell.setCellStyle(green_mod);
                                }
                            }
                            break;

                        case 5:
                            if (numRighe % 2 != 0) {

                                cell.setCellValue(matr_valori[individuo_index - 1][cellnum - 1]);
                                if (individuo_index % 2 != 0) {
                                    cell.setCellStyle(blue_pos);
                                } else {
                                    cell.setCellStyle(green_pos);
                                }
                            } else {
                                //Parte Tipo di modifica

                                cell.setCellValue(matr_mod[aux_valori_index - 1][cellnum - 1]);
                                if (aux_valori_index % 2 != 0) {
                                    cell.setCellStyle(blue_mod);
                                } else {
                                    cell.setCellStyle(green_mod);
                                }
                            }
                            break;

                    }*/

                }
                if (numRighe > 0 && cellnum > contatoreMosse ) {
                    Cell cell = row.createCell(cellnum);
                    if (numRighe % 2 == 0) {
                        cell.setCellStyle(pink);
                        cell.setCellValue(contFitness[individuo_index - 2]);
                    }
                }

            }

        }

        try ( // Output file
                FileOutputStream out = new FileOutputStream("/home/gianni/Documenti/output/" + nome_file + ".xls")) {
            wb.write(out);
            out.close();
        }

        System.out.println("File Excel creato correttamente!");
    }

    /*
    Altro metodo per stampare da Hashtable
    public static void printMap(HashMap map) {
  // Avvio il ciclo su tutti gli elementi (entry)
  // della mappa (map.entrySet())
  for (Map.Entry<Integer, Integer> entry : map.entrySet()) { 
    // Stampo le coppie chiave-valore
    System.out.println("Key = " + entry.getKey());
    System.out.println("Value = " + entry.getValue()); 
  } 
}
     */
    private static void riempiMatr(int[][] matr_valori, String[][] matr_mod, HashMap<Integer,LineaDeformabile> popolazione, double[] contFitness) {
        for (int f = 0; f < popolazione.size(); f++) {

            int index = 0;
            Map map = popolazione.get(f).getQuadratiDeformati();

            //riempio vettore di fitness
            contFitness[f] = popolazione.get(f).getVal_fitness();

            // Costruisce l'iteratore con il metodo dedicato
            Iterator it = map.entrySet().iterator();
            // Verifica con il metodo hasNext() che nella hashmap
            // ci siano altri elementi su cui ciclare
            while (it.hasNext()) {

                // Utilizza il nuovo elemento (coppia chiave-valore)
                // dell'hashmap
                Map.Entry entry = (Map.Entry) it.next();
                // Stampa a schermo la coppia chiave-valore;
                Quadrato aux;
                aux = (Quadrato) entry.getValue();

        //        System.out.println("Key = " + entry.getKey() + "          Tipo di modifica = " + aux.nome_def);
                matr_valori[f][index] = (int) entry.getKey();
                matr_mod[f][index] = (String) aux.nome_def;
                index++;
            }
     //       System.out.println("|||||||||||||||||||||||||||||||||| " +"Individuo "+ (f+1) + " ------ VAL_FITNESS: "+ contFitness[f]+ " |||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        }
    }

}