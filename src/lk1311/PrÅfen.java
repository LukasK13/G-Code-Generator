package lk1311;

import javax.swing.*;

public class Prüfen implements Runnable{

	private String[] temp;
	private String[] Split = {"","","","","",""};	
	private boolean geprüft = false;
	private boolean ProgrammEndeVorhanden = false;
	private double FräseX = 0;
	private double FräseY = 0;
	private double FräseZ = 0;
	private boolean FräskoordinatenOK = true;
	private boolean SpindelAn = false;
	private boolean AbsolutMaß = false;

	public String[] Code;
	public JProgressBar Fortschritt;
	public ProgressbarUpdate PbUpdate;
	public double ZOffset=0;
	public int FehlerInt = 0;	

	public void run() {
		Thread.currentThread().setName("PrüfenThread");
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
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
						FehlerInt++;
						geprüft=true;
					}
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
					FehlerInt++;
					geprüft=true;
				}
			}

			if (i==1) {
				if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istDouble(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
						FehlerInt++;
						geprüft=true;
					}
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
					FehlerInt++;
					geprüft=true;
				}
			}

			if (i==2) {
				if (Split[1].equals("G40") | Split[1].equals("G41") | Split[1].equals("G42")) {
					geprüft = true;
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
					FehlerInt++;
					geprüft=true;
				}
			}

			if (i==3) {
				if (Split[1].equals("G90")) {
					geprüft = true;
					AbsolutMaß = true;
				} else if (Split[1].equals("G91")){
					geprüft = true;
					AbsolutMaß = false;
				} else {
					System.out.println("Fehler bei N" + (i + 1) + "  -  Bitte Header überprüfen!");
					FehlerInt++;
					geprüft=true;
				}
			}

			if (i>3) {
				if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseX = Double.parseDouble(Split[2].substring(1));
						} else {
							FräseX = FräseX + Double.parseDouble(Split[2].substring(1));
						}	
					}	

					else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseY = Double.parseDouble(Split[2].substring(1));
						} else {
							FräseY = FräseY + Double.parseDouble(Split[2].substring(1));
						}	
					}

					else if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseZ = Double.parseDouble(Split[2].substring(1));
						} else {
							FräseZ = FräseZ + Double.parseDouble(Split[2].substring(1));
						}	
						if (FräseZ <= ZOffset && !SpindelAn) {
							System.out.println("Fehler bei N" + (i + 1) + "Fräser im Material nicht rotierend");
							FehlerInt++;
						}
					}

					else if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						geprüft=true;
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseX = Double.parseDouble(Split[2].substring(1));
							FräseY = Double.parseDouble(Split[3].substring(1));
						} else {
							FräseX = FräseX + Double.parseDouble(Split[2].substring(1));
							FräseY = FräseY + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseX = Double.parseDouble(Split[2].substring(1));
							FräseZ = Double.parseDouble(Split[3].substring(1));
						} else {
							FräseX = FräseX + Double.parseDouble(Split[2].substring(1));
							FräseZ = FräseZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
							&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						geprüft=true;
						if (AbsolutMaß) {
							FräseY = Double.parseDouble(Split[2].substring(1));
							FräseZ = Double.parseDouble(Split[3].substring(1));
						} else {
							FräseY = FräseY + Double.parseDouble(Split[2].substring(1));
							FräseZ = FräseZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
						geprüft=true;
						System.out.println("keine Radiuskorrektur wegen G00 X Y Z / G01 X Y Z");
						//keine Korrektur
						if (AbsolutMaß) {
							FräseX = Double.parseDouble(Split[2].substring(1));
							FräseY = Double.parseDouble(Split[3].substring(1));
							FräseZ = Double.parseDouble(Split[3].substring(1));
						} else {
							FräseX = FräseX + Double.parseDouble(Split[2].substring(1));
							FräseY = FräseY + Double.parseDouble(Split[3].substring(1));
							FräseZ = FräseZ + Double.parseDouble(Split[3].substring(1));
						}						
					}

					else {
						FräskoordinatenOK = false;
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}

				else if (Split[1].equals("G2") | Split[1].equals("G02") | Split[1].equals("G3") | Split[1].equals("G03")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
							&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
							&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
							&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
						if (FräskoordinatenOK) {
							if (AbsolutMaß) {
								if (Math.sqrt((FräseX - Double.parseDouble(Split[4].substring(1))) * (FräseX - Double.parseDouble(Split[4].substring(1)))
										+ (FräseY - Double.parseDouble(Split[5].substring(1))) * (FräseY - Double.parseDouble(Split[5].substring(1)))) ==
										Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												* (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												+ (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1)))
												* (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) )) {
									geprüft = true;
									FräseX = Double.parseDouble(Split[4].substring(1));
									FräseY = Double.parseDouble(Split[5].substring(1));
								} else {
									System.out.println("Fehler bei N" + (i + 1) + " Endpunkt liegt nicht auf kreisbahn");
									FehlerInt++;
									geprüft=true;
								}
							} else {
								if (Math.sqrt(Double.parseDouble(Split[4].substring(1)) * Double.parseDouble(Split[4].substring(1))
										+ Double.parseDouble(Split[5].substring(1)) * Double.parseDouble(Split[5].substring(1))) ==
										Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												* (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1)))
												+ (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1)))
												* (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) )) {
									geprüft = true;
									FräseX = FräseX + Double.parseDouble(Split[4].substring(1));
									FräseY = FräseY + Double.parseDouble(Split[5].substring(1));
								} else {
									System.out.println("Fehler bei N" + (i + 1) + " Endpunkt liegt nicht auf kreisbahn");
									FehlerInt++;
									geprüft=true;
								}
							}

						} else {
							geprüft=true;
						}

					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}			

				else if (Split[1].equals("G4") | Split[1].equals("G04") | Split[1].equals("G5") | Split[1].equals("G05")) {
					if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}

				else if (Split[1].equals("G40") | Split[1].equals("G41") | Split[1].equals("G42")) {
					geprüft=true;
				}

				else if (Split[1].equals("G90")) {
					AbsolutMaß = true;
					geprüft=true;
				}

				else if (Split[1].equals("G91")) {
					AbsolutMaß = false;
					geprüft=true;
				}

				else if (Split[1].equals("G72")) {
					if ((Split[2].startsWith("N") | Split[2].startsWith("n")) && istInteger(Split[2].substring(1)) && istInteger(Split[3])) {
						if (Integer.parseInt(Split[2].substring(1))<i) {
							geprüft=true;
						} else {
							System.out.println("Fehler bei N" + (i + 1));
							FehlerInt++;
							geprüft=true;
						}
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}

				else if (Split[1].equals("M00") | Split[1].equals("M0")) {
					geprüft=true;
				}

				else if (Split[1].equals("M02") | Split[1].equals("M2")) {
					ProgrammEndeVorhanden = true;
					geprüft=true;
				}

				else if (Split[1].equals("M03") | Split[1].equals("M3")) {
					SpindelAn = true;
					geprüft=true;
				}

				else if (Split[1].equals("M05") | Split[1].equals("M5")) {
					SpindelAn = false;
					geprüft=true;
				}

				else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					geprüft=true;
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}

				else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
						geprüft=true;
					} else {
						System.out.println("Fehler bei N" + (i + 1));
						FehlerInt++;
						geprüft=true;
					}
				}
			}

			if (geprüft==false) {
				System.out.println("N" + (i + 1) + " konnte nicht geprüft werden. Bitte Syntax überprüfen!");
				FehlerInt++;
			}
			geprüft=false;
			PbUpdate.Fortschritt = (int) (i+1) * 100 /Code.length;

			for (int j=0; j<Split.length; j++) {	//String Split leeren
				Split[j] = "";
			}
		}
		if (!FräskoordinatenOK) {
			System.out.println("Es konnten nicht alle Parameter überprüft werden, da bereits Fehlerhafte Koordinaten gefunden wurden. Bitte die aufgelisteten Fehler Korrigieren und Prüfung erneut starten!");
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
