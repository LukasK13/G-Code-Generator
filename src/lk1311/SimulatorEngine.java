package lk1311;

import java.awt.*;

import javax.swing.*;

public class SimulatorEngine implements Runnable {

	private String[] temp;
	private String[] Split = {"","","","","",""};	
	private double Z;
	private double A;
	private int k=0;
	private final double Pixelbreite = 0.24;
	private int Korrektur=0; //0=keine Radiuskorrektur 1=links 2=rechts
	private int Maﬂ=0; //0=Absolutmaﬂ 1=Relativmaﬂ
	private boolean SpindelAn=false;
	private double Vorschub=0;
	private double Drehzahl=0;
	private int Tool=1;
	private double alpha = 0;
	private double radius = 0;
	private double beta = 0;

	public String[] CodeSplit;
	public Turtle t;
	public JTextArea CNCStatus;
	public JButton StopBtn;
	public  String[][] Werkzeugdaten;
	public double maﬂstab;
	public double StreckeIn=0;
	public double StreckeOut=0;
	public boolean FahrwegAuﬂen = true;
	public boolean Pause = false;
	public ProgressbarUpdate PbUpdate;
	public Thread PbUpdateThread;

	public void run() {
		Z=0;
		StreckeIn=0;
		StreckeOut=0;
		PbUpdate.Fortschritt=0;
		t.moveto(0, 0);
		t.setForeground(Color.blue);
		ZeichenSchleife:
			for (int i=0; i<CodeSplit.length; i++) {
				while (Pause) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						//e.printStackTrace();
					}
					if (Thread.interrupted()) {
						break ZeichenSchleife;
					}
				}

				if (Thread.interrupted()) {
					break;
				}

				temp = CodeSplit[i].split(" ");

				for (int j=0; j<temp.length; j++) {
					Split[j] = temp[j];
				}

				if (i==0) {
					if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
						if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
							Tool=Integer.parseInt(Split[1].substring(1));
						} 
					} 
				}

				if (i==1) {
					if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istDouble(Split[2].substring(1))) {
						if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
							Vorschub=Integer.parseInt(Split[1].substring(1));
							Drehzahl=Integer.parseInt(Split[2].substring(1));
						} 
					}
				}

				if (i==2) {
					if (Split[1].equals("G40")) {
						Korrektur=0;
					} else if (Split[1].equals("G41")) {
						Korrektur=1;
					} else if (Split[1].equals("G42")) {
						Korrektur=2;
					}
				}

				if (i==3) {
					if (Split[1].equals("G90")) {
						Maﬂ=0;
					} else if(Split[1].equals("G91")) {
						Maﬂ=1;
					}
				}

				if (i>3) {
					if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
						//G01 X
						if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
							if (Z<0) {
								if (Maﬂ==0) {
									StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1)) - Strecke(t.turtleX);
									t.setForeground(Color.blue);
									t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
								} else if (Maﬂ==1) {
									StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1));
									t.setForeground(Color.blue);
									t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
								}							
							} else {
								if (Maﬂ==0) {
									StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX);
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									}
								} else if (Maﬂ==1) {
									StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1));
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										t.moveto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									}								
								}							
							}					
						}

						//G01 Y
						else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
							if (Z<0) {
								if (Maﬂ==0) {
									StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1)) - Strecke(t.turtleY);
									t.setForeground(Color.blue);
									t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
								} else if (Maﬂ==1) {
									StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1));
									t.setForeground(Color.blue);
									t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
								}							
							} else {
								if (Maﬂ==0) {
									StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleY);
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										t.moveto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
									}
								} else if (Maﬂ==1) {
									StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1));
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										t.moveto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
									}								
								}							
							}					
						}

						//G01 Z
						if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
							if (Maﬂ==0) {
								Z = Double.parseDouble(Split[2].substring(1));	
							} else {
								Z = Z + Double.parseDouble(Split[2].substring(1));
							}
						}
						
						//G01 A
						if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
							if (Maﬂ==0) {
								A = Double.parseDouble(Split[2].substring(1));	
							} else {
								A = A + Double.parseDouble(Split[2].substring(1));
							}
						}

						//G01 X Y
						else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
								&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
							if (Z<0) {
								if (Maﬂ==0) {
									StreckeIn = StreckeIn + Math.sqrt((Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX)) * (Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX)) + 
											(Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY)) * (Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY)));
									t.setForeground(Color.blue);
									t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
								} else if (Maﬂ==1) {
									StreckeIn = StreckeIn + Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									t.setForeground(Color.blue);
									t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
								}							

							} else {
								if (Maﬂ==0) {
									StreckeOut = StreckeOut + Math.sqrt((Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX)) * (Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX)) + 
											(Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY)) * (Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY)));
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
									}
								} else if (Maﬂ==1) {
									StreckeOut = StreckeOut + Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									if (FahrwegAuﬂen) {
										t.setForeground(Color.red);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										t.moveto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
									}								
								}							
							}
						}

						//G01 X Z
						else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
								&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
							if (Z<0) {
								if (Maﬂ==0) {
									double RelativX = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX);
									if (Double.parseDouble(Split[3].substring(1))<0) {
										StreckeIn = StreckeIn + RelativX;
										t.setForeground(Color.blue);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
										StreckeIn = StreckeIn + Faktor*RelativX;
										StreckeOut = StreckeOut + (1-Faktor)*RelativX;
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY);
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										} else {
											t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										}										
									}
									Z = Double.parseDouble(Split[3].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[3].substring(1)))<0) {
										StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
										StreckeIn = StreckeIn + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeOut = StreckeOut + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY);
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										} else {
											t.moveto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										}										
									}
									Z = Z + Double.parseDouble(Split[3].substring(1));
								}							

							} else {
								if (Maﬂ==0) {
									double RelativX = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX);
									if (Double.parseDouble(Split[3].substring(1))>=0) {
										StreckeOut = StreckeOut + RelativX;
										t.setForeground(Color.red);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
										StreckeOut = StreckeOut + Faktor*RelativX;
										StreckeIn = StreckeIn + (1-Faktor)*RelativX;
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY);
										} else {
											t.moveto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY);
										}										
										t.setForeground(Color.blue);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									}
									Z = Double.parseDouble(Split[3].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[3].substring(1)))>=0) {
										StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.red);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									} else {
										double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
										StreckeOut = StreckeOut + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeIn = StreckeIn + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY);
										} else {
											t.moveto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY);
										}										
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
									}
								}
								Z = Z + Double.parseDouble(Split[3].substring(1));
							}
						}

						//G01 Y Z
						else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
								&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
							if (Z<0) {
								if (Maﬂ==0) {
									double RelativY = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleY);
									if (Double.parseDouble(Split[3].substring(1))<0) {
										StreckeIn = StreckeIn + RelativY;
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
										StreckeIn = StreckeIn + Faktor*RelativY;
										StreckeOut = StreckeOut + (1-Faktor)*RelativY;
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, t.turtleY + Pixel(Faktor*RelativY));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
										} else {
											t.moveto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
										}										
									}
									Z = Double.parseDouble(Split[3].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[3].substring(1)))<0) {
										StreckeIn = StreckeIn + Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
										StreckeIn = StreckeIn + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeOut = StreckeOut + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, t.turtleY + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
										} else {
											t.moveto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
										}
									}
									Z = Z + Double.parseDouble(Split[3].substring(1));
								}							

							} else {
								if (Maﬂ==0) {
									double RelativY = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleY);
									if (Double.parseDouble(Split[3].substring(1))>=0) {
										StreckeOut = StreckeOut + RelativY;
										t.setForeground(Color.red);
										t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
										StreckeOut = StreckeOut + Faktor*RelativY;
										StreckeIn = StreckeIn + (1-Faktor)*RelativY;
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX, t.turtleY + Pixel(Faktor*RelativY));
										} else {
											t.moveto(t.turtleX, t.turtleY + Pixel(Faktor*RelativY));
										}										
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
									}
									Z = Double.parseDouble(Split[3].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[3].substring(1)))>=0) {
										StreckeOut = StreckeOut + Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.red);
										t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
									} else {
										double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
										StreckeOut = StreckeOut + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeIn = StreckeIn + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX, t.turtleY + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))));
										} else {
											t.moveto(t.turtleX, t.turtleY + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))));
										}										
										t.setForeground(Color.blue);
										t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
									}
									Z = Z + Double.parseDouble(Split[3].substring(1));
								}							
							}
						} 

						//G01 X Y Z
						else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
								&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
							if (Z<0) {
								if (Maﬂ==0) {
									double RelativX = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX);
									double RelativY = Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY);
									if (Double.parseDouble(Split[4].substring(1))<0) {
										StreckeIn = StreckeIn + Math.sqrt(RelativX * RelativX + RelativY * RelativY);
										t.setForeground(Color.blue);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[4].substring(1))-Z);
										StreckeIn = StreckeIn + Math.sqrt(Faktor*RelativX * Faktor*RelativX + Faktor*RelativY * Faktor*RelativY);
										StreckeOut = StreckeOut + Math.sqrt((1-Faktor)*RelativX * (1-Faktor)*RelativX + (1-Faktor)*RelativY * (1-Faktor)*RelativY);
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY + Pixel(Faktor*RelativY));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
										} else {
											t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
										}
									}
									Z = Double.parseDouble(Split[4].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[4].substring(1)))<0) {
										StreckeIn = StreckeIn + Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
												Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										double Faktor=-Z/Double.parseDouble(Split[4].substring(1));
										StreckeIn = StreckeIn + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeOut = StreckeOut + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Faktor*Double.parseDouble(Split[3].substring(1))));
										if(FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
										} else {
											t.moveto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));						
										}
									}
									Z = Z + Double.parseDouble(Split[4].substring(1));
								}							

							} else {
								if (Maﬂ==0) {
									double RelativX = Double.parseDouble(Split[2].substring(1))-Strecke(t.turtleX);
									double RelativY = Double.parseDouble(Split[3].substring(1))-Strecke(t.turtleY);
									if (Double.parseDouble(Split[4].substring(1))>=0) {
										StreckeOut = StreckeOut + Math.sqrt(RelativX * RelativX + RelativY * RelativY);
										t.setForeground(Color.red);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										double Faktor=-Z/(Double.parseDouble(Split[4].substring(1))-Z);
										StreckeOut = StreckeOut + Math.sqrt(Faktor*RelativX * Faktor*RelativX + Faktor*RelativY * Faktor*RelativY);
										StreckeIn = StreckeIn + Math.sqrt((1-Faktor)*RelativX * (1-Faktor)*RelativX + (1-Faktor)*RelativY * (1-Faktor)*RelativY);
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY + Pixel(Faktor*RelativY));
										} else {
											t.moveto(t.turtleX + Pixel(Faktor*RelativX), t.turtleY + Pixel(Faktor*RelativY));
										}										
										t.setForeground(Color.blue);
										t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
									}
									Z = Double.parseDouble(Split[4].substring(1));

								} else if (Maﬂ==1) {
									if ((Z + Double.parseDouble(Split[4].substring(1)))>=0) {
										StreckeOut = StreckeOut + Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
												Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
										t.setForeground(Color.red);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
									} else {
										double Faktor=-Z/Double.parseDouble(Split[4].substring(1));
										StreckeOut = StreckeOut + Faktor*Double.parseDouble(Split[2].substring(1));
										StreckeIn = StreckeIn + (1-Faktor)*Double.parseDouble(Split[2].substring(1));
										if (FahrwegAuﬂen) {
											t.setForeground(Color.red);
											t.drawto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Faktor*Double.parseDouble(Split[3].substring(1))));
										} else {
											t.moveto(t.turtleX + Pixel(Faktor*Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Faktor*Double.parseDouble(Split[3].substring(1))));
										}
										t.setForeground(Color.blue);
										t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
									}
									Z = Z + Double.parseDouble(Split[4].substring(1));
								}							
							}
						}
					}				

					else if (Split[1].equals("G2") | Split[1].equals("G02")) {
						if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
								&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
								&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
								&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
							if (Maﬂ==0) {
								radius = Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1))) * (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1))) + 
										(Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) * (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)),
										Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), radius, true);
							} else {
								radius = Math.sqrt(Double.parseDouble(Split[4].substring(1)) * Double.parseDouble(Split[4].substring(1)) + 
										Double.parseDouble(Split[5].substring(1)) * Double.parseDouble(Split[5].substring(1)));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Strecke(t.turtleX) + Double.parseDouble(Split[4].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[5].substring(1)),
										Strecke(t.turtleX) + Double.parseDouble(Split[2].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[3].substring(1)), radius, true);
							}
						} 
					}		

					else if (Split[1].equals("G3") | Split[1].equals("G03")) {
						if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
								&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
								&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
								&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
							if (Maﬂ==0) {
								radius = Math.sqrt((Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1))) * (Double.parseDouble(Split[2].substring(1)) - Double.parseDouble(Split[4].substring(1))) + 
										(Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))) * (Double.parseDouble(Split[3].substring(1)) - Double.parseDouble(Split[5].substring(1))));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)),
										Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), radius, false);
							} else {
								radius = Math.sqrt(Double.parseDouble(Split[4].substring(1)) * Double.parseDouble(Split[4].substring(1)) + 
										Double.parseDouble(Split[5].substring(1)) * Double.parseDouble(Split[5].substring(1)));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Strecke(t.turtleX) + Double.parseDouble(Split[4].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[5].substring(1)),
										Strecke(t.turtleX) + Double.parseDouble(Split[2].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[3].substring(1)), radius, false);
							}
						} 
					}		

					else if (Split[1].equals("G4") | Split[1].equals("G04")) {
						if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
								&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
							if (Maﬂ==0) {
								if (Strecke(t.turtleY) >= Double.parseDouble(Split[3].substring(1))) {
									radius = Math.sqrt((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) * (Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) + 
											(Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))) * (Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))));							
									alpha = Math.acos((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) / radius); 
								} else {
									radius = Math.sqrt((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) * (Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) + 
											(Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))) * (Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))));
									alpha = Math.PI * 2 - Math.acos((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) / radius); 								
								}
								beta = alpha - (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
								if (beta < 0) {
									beta = Math.PI * 2 + beta;
								}		
								while (beta > 2 * Math.PI){
									beta = beta - 2 * Math.PI;
								}	
								//System.out.println("Alpha: " + alpha / (Math.PI * 2) * 360);	
								//System.out.println("Beta: " + beta / (Math.PI * 2) * 360);
								double EndeX = Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
								double EndeY = Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), EndeX, EndeY, radius, true);
							} else {
								if (Double.parseDouble(Split[3].substring(1)) <= 0) {
									radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius); 
								} else {
									radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius) + Math.PI; 
								}
								beta = alpha - (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
								if (beta < 0) {
									beta = Math.PI * 2 + beta;
								}		
								while (beta > 2 * Math.PI){
									beta = beta - 2 * Math.PI;
								}		
								//System.out.println("Alpha: " + alpha / (Math.PI * 2) * 360);	
								//System.out.println("Beta: " + beta / (Math.PI * 2) * 360);
								double EndeX = Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1)) + Strecke(t.turtleX);
								double EndeY = Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1)) + Strecke(t.turtleY);
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Strecke(t.turtleX) + Double.parseDouble(Split[2].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[3].substring(1)), EndeX, EndeY, radius, true);
							}
						} 
					}

					else if (Split[1].equals("G5") | Split[1].equals("G05")) {
						if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
								&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
							if (Maﬂ==0) {
								if (Strecke(t.turtleY) >= Double.parseDouble(Split[3].substring(1))) {
									radius = Math.sqrt((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) * (Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) + 
											(Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))) * (Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))));							
									alpha = Math.acos((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) / radius); 
								} else {
									radius = Math.sqrt((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) * (Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) + 
											(Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))) * (Strecke(t.turtleY) - Double.parseDouble(Split[3].substring(1))));
									alpha = Math.PI * 2 - Math.acos((Strecke(t.turtleX) - Double.parseDouble(Split[2].substring(1))) / radius); 								
								}
								beta = alpha + (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
								if (beta < 0) {
									beta = Math.PI * 2 + beta;
								}		
								while (beta > 2 * Math.PI){
									beta = beta - 2 * Math.PI;
								}	
								//System.out.println("Alpha: " + alpha / (Math.PI * 2) * 360);	
								//System.out.println("Beta: " + beta / (Math.PI * 2) * 360);
								double EndeX = Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
								double EndeY = Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), EndeX, EndeY, radius, false);
							} else {
								if (Double.parseDouble(Split[3].substring(1)) <= 0) {
									radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius); 
								} else {
									radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
											Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
									alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius) + Math.PI; 
								}
								beta = alpha + (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
								if (beta < 0) {
									beta = Math.PI * 2 + beta;
								}		
								while (beta > 2 * Math.PI){
									beta = beta - 2 * Math.PI;
								}		
								//System.out.println("Alpha: " + alpha / (Math.PI * 2) * 360);	
								//System.out.println("Beta: " + beta / (Math.PI * 2) * 360);
								double EndeX = Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1)) + Strecke(t.turtleX);
								double EndeY = Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1)) + Strecke(t.turtleY);
								Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Strecke(t.turtleX) + Double.parseDouble(Split[2].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[3].substring(1)), EndeX, EndeY, radius, false);
							}
						} 
					}

					else if (Split[1].equals("G40")) {
						Korrektur=0;
					}

					else if (Split[1].equals("G41")) {
						Korrektur=1;
					}

					else if (Split[1].equals("G42")) {
						Korrektur=2;
					}

					else if (Split[1].equals("G90")) {
						Maﬂ=0;
					}

					else if (Split[1].equals("G91")) {
						Maﬂ=1;
					}

					else if (Split[1].equals("G72")) {
						if ((Split[2].startsWith("N") | Split[2].startsWith("n")) && istInteger(Split[2].substring(1)) && istInteger(Split[3])) {
							if (Integer.parseInt(Split[2].substring(1))<i) {

							} 
						}
					}

					else if (Split[1].equals("M00") | Split[1].equals("M0")) {
						CNCStatus.append("Programm angehalten");
						if (t.drawDynamic) {
							Pause = true;
							StopBtn.setText("Fortsetzen");
						}
					}

					else if (Split[1].equals("M02") | Split[1].equals("M2")) {
						PbUpdate.Fortschritt = 100;
						CNCStatus.append("Programm beendet");
						break;
					}

					else if (Split[1].equals("M03") | Split[1].equals("M3")) {
						SpindelAn=true;
					}

					else if (Split[1].equals("M05") | Split[1].equals("M5")) {
						SpindelAn=false;
					}

					else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
						if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
							Drehzahl=Double.parseDouble(Split[1].substring(1));
						} 
					}

					else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
						Vorschub=Double.parseDouble(Split[1].substring(1));
					}

					else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
						if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
							Vorschub=Double.parseDouble(Split[1].substring(1));
							Drehzahl=Double.parseDouble(Split[2].substring(1));
						} 
					}

					else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
						if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
							Tool=Integer.parseInt(Split[1].substring(1));
						} 
					}
				}
				CNCStatus.setText("");
				CNCStatus.append("Position:   X:" + Strecke(t.turtleX) + "mm   Y:" + Strecke(t.turtleY) + "mm   Z:" + Z + "mm   A:" + A + "∞" + "\n");
				CNCStatus.append("Werkzeug: T" + Tool + "   Durchmesser: " + Werkzeugdaten[Tool-1][1] + "mm   L‰nge: " + Werkzeugdaten[Tool-1][2] + "mm" + "\n");
				CNCStatus.append("Vorschub F: " + Vorschub + "mm/min   Drehzahl: " + Drehzahl + "U/min" + "\n");
				if (Korrektur==0) {
					CNCStatus.append("Werkzeugkorrektur: keine (G40)" + "\n");
				} else if (Korrektur==1) {
					CNCStatus.append("Werkzeugkorrektur: links (G41)" + "\n");
				} else if (Korrektur==2) {
					CNCStatus.append("Werkzeugkorrektur: rechts (G42)" + "\n");
				}

				if (Maﬂ==0) {
					CNCStatus.append("Maﬂ: Absolutmaﬂ (G90)" + "\n");
				} else if (Maﬂ==1) {
					CNCStatus.append("Maﬂ: Relativmaﬂ (G91)" + "\n");
				}

				if (SpindelAn) {
					CNCStatus.append("Spindel ist an" + "\n");
				} else {
					CNCStatus.append("Spindel ist aus" + "\n");
				}

				CNCStatus.append("Strecke im Material: " + (int) StreckeIn + "mm     Strecke auﬂen: " + (int) StreckeOut + "mm     Strecke gesamt: " + (int) (StreckeIn + StreckeOut) + "mm" + "\n");
				PbUpdate.Fortschritt = (int) (i+1) * 100 /CodeSplit.length;

				for (int j=0; j<Split.length; j++) {	//String Split leeren
					Split[j] = "";
				}

			}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			//e.printStackTrace();
		}

		k=0;
		while (PbUpdateThread.isAlive()) {
			PbUpdateThread.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			if (k==5) {
				System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
				break;
			}
		}
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

	private double Strecke(double pixel) {
		double strecke = pixel * Pixelbreite / maﬂstab;
		return strecke;
	}

	private double Pixel(double Strecke) {
		double pixel = Strecke * maﬂstab / Pixelbreite;
		return pixel;
	}

	private void Kreis(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {
		//System.out.println("Start: " + StartX + "/" + StartY + "   Mitte: " + MitteX + "/" + MitteY + "   Ende: " + EndeX + "/" + EndeY);
		/*
		 * Koordinatenquadranten
		 * Nullpunkt = kreismittelpunkt
		 * 
		 * 		  |
		 *   (1)  |	 (1)
		 * -------|-------
		 *   (2)  |  (2)
		 *		  |  
		 */
		int Startquadrant = 0;
		int Endquadrant = 0;

		Startquadrant = Quadrant(StartX, StartY, MitteX, MitteY);
		Endquadrant = Quadrant(EndeX, EndeY, MitteX, MitteY);
		//System.out.println("Startquadrant: " + Startquadrant + "   Endquadrant: " + Endquadrant);

		if (Uhrzeigersinn) { 
			if (Startquadrant == 1 && Endquadrant == 1) {
				if (StartX < EndeX) {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				} else {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
					Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
					Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				}   			
			} else if (Startquadrant == 2 && Endquadrant == 2) {
				if (StartX < EndeX) {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
					Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
					Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				} else {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				}    			
			} else if (Startquadrant == 1 && Endquadrant == 2) {
				Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
				Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
			} else if (Startquadrant == 2 && Endquadrant == 1) {
				Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
				Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
			}    		
		} else {    		
			if (Startquadrant == 1 && Endquadrant == 1) {
				if (StartX < EndeX) {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
					Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
					Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				} else {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				}   			
			} else if (Startquadrant == 2 && Endquadrant == 2) {
				if (StartX < EndeX) {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				} else {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
					Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
					Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				}			
			} else if (Startquadrant == 1 && Endquadrant == 2) {
				Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
				Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
			} else if (Startquadrant == 2 && Endquadrant == 1) {
				Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
				Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
			}
		}
	} 

	private int Quadrant(double PunktX, double PunktY, double MitteX, double MitteY) {
		if (PunktY>=MitteY) {
			return 1;
		} else {
			return 2;
		}
	}

	private void Quadrant1Zeichnen(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {
		int StartXPix = (int) Pixel(StartX);
		int StartYPix = (int) Pixel(StartY);
		int MitteXPix = (int) Pixel(MitteX);
		int MitteYPix = (int) Pixel(MitteY);
		int EndeXPix = (int) Pixel(EndeX);
		int RadiusPix = (int) Pixel(Radius);

		t.drawto(StartXPix, StartYPix);
		if (StartXPix <= EndeXPix) {
			for (int i = StartXPix - MitteXPix; i <= EndeXPix - MitteXPix; i++) {
				if ((RadiusPix * RadiusPix - i * i) > 0) {
					t.drawto(i + MitteXPix, MitteYPix + Math.sqrt(RadiusPix * RadiusPix - i * i));
				}
			}   	
		} else {
			for (int i = StartXPix - MitteXPix; i >= EndeXPix - MitteXPix; i--) {
				if ((RadiusPix * RadiusPix - i * i) > 0) {
					t.drawto(i + MitteXPix, MitteYPix + Math.sqrt(RadiusPix * RadiusPix - i * i));
				}
			}   	
		}
		t.drawto(Pixel(EndeX), Pixel(EndeY));
	}

	private void Quadrant2Zeichnen(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {
		int StartXPix = (int) Pixel(StartX);
		int StartYPix = (int) Pixel(StartY);
		int MitteXPix = (int) Pixel(MitteX);
		int MitteYPix = (int) Pixel(MitteY);
		int EndeXPix = (int) Pixel(EndeX);
		int RadiusPix = (int) Pixel(Radius);

		t.drawto(StartXPix, StartYPix);
		if (StartXPix <= EndeXPix) {
			for (int i = StartXPix - MitteXPix; i <= EndeXPix - MitteXPix; i++) {
				if ((RadiusPix * RadiusPix - i * i) > 0) {
					t.drawto(i + MitteXPix, MitteYPix - Math.sqrt(RadiusPix * RadiusPix - i * i));
				}
			}   	
		} else {
			for (int i = StartXPix - MitteXPix; i >= EndeXPix - MitteXPix; i--) {
				if ((RadiusPix * RadiusPix - i * i) > 0) {
					t.drawto(i + MitteXPix, MitteYPix - Math.sqrt(RadiusPix * RadiusPix - i * i));
				}
			}   	
		}

		t.drawto(Pixel(EndeX), Pixel(EndeY));
	}
}

