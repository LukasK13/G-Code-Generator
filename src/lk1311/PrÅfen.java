package lk1311;

import javax.swing.*;

public class Pr�fen implements Runnable{

	private String[] temp;
	private String[] Split = {"","","","","",""};	
	private boolean gepr�ft = false;
	private boolean ProgrammEndeVorhanden = false;
	private double Fr�seX = 0;
	private double Fr�seY = 0;
	private double Fr�seZ = 0;
	private boolean Fr�skoordinatenOK = true;
	private boolean SpindelAn = false;
	private boolean AbsolutMa� = false;

	public String[] Code;
	public JProgressBar Fortschritt;
	public ProgressbarUpdate PbUpdate;
	public double ZOffset=0;
	public int FehlerInt = 0;	

	public void run() {
		Thread.currentThread().setName("Pr�fenThread");
		FehlerInt=0;
		for (int i=0; i<Code.length; i++) {
			temp = Code[i].split(" ");

			for (int j=0; j<temp.length; j++) {
				Split[j] = temp[j];
			}
			if (Split[0].startsWith("N") == true) {
				Split[0]=Split[1];
				Split[1]=Split[2];
				Split[2]=Split[3];
				Split[3]=Split[4];
				Split[4]=Split[5];
				Split[5]="";
			}

			if (Split[0].startsWith("N") == false) {		
				Split[5]=Split[4];
				Split[4]=Split[3];
				Split[3]=Split[2];
				Split[2]=Split[1];
				Split[1]=Split[0];
				Split[0]="N" + (i + 1);

				int j=1;
				Code[i] = Split[0];

				while (Split[j] != "") {
					Code[i] = Code[i].concat(" " + Split[j]);
					j++;
					if (j>=Split.length) {
						break;
					}
				}
			} 

			if (i==0) {
				if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
						FehlerInt++;
						gepr�ft=true;
					}
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
					FehlerInt++;
					gepr�ft=true;
				}
			}

			if (i==1) {
				if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istDouble(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
						FehlerInt++;
						gepr�ft=true;
					}
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
					FehlerInt++;
					gepr�ft=true;
				}
			}

			if (i==2) {
				if (Split[1].equals("G40") | Split[1].equals("G41") | Split[1].equals("G42")) {
					gepr�ft = true;
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
					FehlerInt++;
					gepr�ft=true;
				}
			}

			if (i==3) {
				if (Split[1].equals("G90")) {
					gepr�ft = true;
					AbsolutMa� = true;
				} else if (Split[1].equals("G91")){
					gepr�ft = true;
					AbsolutMa� = false;
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header �berpr�fen!");
					FehlerInt++;
					gepr�ft=true;
				}
			}

			if (i>3) {
				if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seX = Double.parseDouble(Split[2].substring(1));
						} else {
							Fr�seX = Fr�seX + Double.parseDouble(Split[2].substring(1));
						}	
					}	

					else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seY = Double.parseDouble(Split[2].substring(1));
						} else {
							Fr�seY = Fr�seY + Double.parseDouble(Split[2].substring(1));
						}	
					}

					else if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seZ = Double.parseDouble(Split[2].substring(1));
						} else {
							Fr�seZ = Fr�seZ + Double.parseDouble(Split[2].substring(1));
						}	
						if (Fr�seZ <= ZOffset && !SpindelAn) {
							System.out.println("Fehler bei N" + (i + 1) + "Fr�ser im Material nicht rotierend");
							FehlerInt++;
						}
					}

					else if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						gepr�ft=true;
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seX = Double.parseDouble(Split[2].substring(1));
							Fr�seY = Double.parseDouble(Split[3].substring(1));
						} else {
							Fr�seX = Fr�seX + Double.parseDouble(Split[2].substring(1));
							Fr�seY = Fr�seY + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seX = Double.parseDouble(Split[2].substring(1));
							Fr�seZ = Double.parseDouble(Split[3].substring(1));
						} else {
							Fr�seX = Fr�seX + Double.parseDouble(Split[2].substring(1));
							Fr�seZ = Fr�seZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						gepr�ft=true;
						if (AbsolutMa�) {
							Fr�seY = Double.parseDouble(Split[2].substring(1));
							Fr�seZ = Double.parseDouble(Split[3].substring(1));
						} else {
							Fr�seY = Fr�seY + Double.parseDouble(Split[2].substring(1));
							Fr�seZ = Fr�seZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
						gepr�ft=true;
						System.out.println("keine Radiuskorrektur wegen G00 X Y Z / G01 X Y Z");
						//keine Korrektur
						if (AbsolutMa�) {
							Fr�seX = Double.parseDouble(Split[2].substring(1));
							Fr�seY = Double.parseDouble(Split[3].substring(1));
							Fr�seZ = Double.parseDouble(Split[3].substring(1));
						} else {
							Fr�seX = Fr�seX + Double.parseDouble(Split[2].substring(1));
							Fr�seY = Fr�seY + Double.parseDouble(Split[3].substring(1));
							Fr�seZ = Fr�seZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else {
						Fr�skoordinatenOK = false;
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}

				else if (Split[1].equals("G2") | Split[1].equals("G02") | Split[1].equals("G3") | Split[1].equals("G03")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
							&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
							&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
							&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
						if (Fr�skoordinatenOK) {
							if (AbsolutMa�) {
								if (Math.sqrt((Fr�seX - Double.parseDouble(Split[4].substring(1))) * (Fr�seX - Double.parseDouble(Split[4].substring(1)))
										+ (Fr�seY - Double.parseDouble(Split[5].substring(1))) * (Fr�seY - Double.parseDouble(Split[5].substring(1)))) ==
										Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												* (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												+ (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1)))
												* (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) )) {
									gepr�ft = true;
									Fr�seX = Double.parseDouble(Split[4].substring(1));
									Fr�seY = Double.parseDouble(Split[5].substring(1));
								} else {
									System.out.println("Fehler bei N" + (i + 1) + " Endpunkt liegt nicht auf kreisbahn");
									FehlerInt++;
									gepr�ft=true;
								}
							} else {
								if (Math.sqrt(Double.parseDouble(Split[4].substring(1)) * Double.parseDouble(Split[4].substring(1))
										+ Double.parseDouble(Split[5].substring(1)) * Double.parseDouble(Split[5].substring(1))) ==
										Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												* (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												+ (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1)))
												* (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) )) {
									gepr�ft = true;
									Fr�seX = Fr�seX + Double.parseDouble(Split[4].substring(1));
									Fr�seY = Fr�seY + Double.parseDouble(Split[5].substring(1));
								} else {
									System.out.println("Fehler bei N" + (i + 1) + " Endpunkt liegt nicht auf kreisbahn");
									FehlerInt++;
									gepr�ft=true;
								}
							}

						} else {
							gepr�ft=true;
						}

					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}			

				else if (Split[1].equals("G4") | Split[1].equals("G04") | Split[1].equals("G5") | Split[1].equals("G05")) {
					if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}

				else if (Split[1].equals("G40") | Split[1].equals("G41") | Split[1].equals("G42")) {
					gepr�ft=true;
				}

				else if (Split[1].equals("G90")) {
					AbsolutMa� = true;
					gepr�ft=true;
				}

				else if (Split[1].equals("G91")) {
					AbsolutMa� = false;
					gepr�ft=true;
				}

				else if (Split[1].equals("G72")) {
					if ((Split[2].startsWith("N") | Split[2].startsWith("n")) && istInteger(Split[2].substring(1)) && istInteger(Split[3])) {
						if (Integer.parseInt(Split[2].substring(1))<i) {
							gepr�ft=true;
						} else {
							System.out.println("Fehler bei N" + (i + 1));
							FehlerInt++;
							gepr�ft=true;
						}
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}

				else if (Split[1].equals("M00") | Split[1].equals("M0")) {
					gepr�ft=true;
				}

				else if (Split[1].equals("M02") | Split[1].equals("M2")) {
					ProgrammEndeVorhanden = true;
					gepr�ft=true;
				}

				else if (Split[1].equals("M03") | Split[1].equals("M3")) {
					SpindelAn = true;
					gepr�ft=true;
				}

				else if (Split[1].equals("M05") | Split[1].equals("M5")) {
					SpindelAn = false;
					gepr�ft=true;
				}

				else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					gepr�ft=true;
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}

				else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
						gepr�ft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						gepr�ft=true;
					}
				}
			}

			if (gepr�ft==false) {
				System.out.println("N" + (i + 1) + " konnte nicht gepr�ft werden. Bitte Syntax �berpr�fen!");
				FehlerInt++;
			}
			gepr�ft=false;
			PbUpdate.Fortschritt = (int) (i+1) * 100 /Code.length;

			for (int j=0; j<Split.length; j++) {	//String Split leeren
				Split[j] = "";
			}
		}
		if (!Fr�skoordinatenOK) {
			System.out.println("Es konnten nicht alle Parameter �berpr�ft werden, da bereits Fehlerhafte Koordinaten gefunden wurden. Bitte die aufgelisteten Fehler Korrigieren und Pr�fung erneut starten!");
		}
		if (!ProgrammEndeVorhanden) {
			System.out.println("Programmende fehlt");
		}
		System.out.println("Es wurde(n) " + FehlerInt + " Fehler gefunden");
	}

	private boolean istDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean istInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

}
