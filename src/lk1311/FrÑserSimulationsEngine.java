package lk1311;

import java.awt.*;

public class Fr‰serSimulationsEngine implements Runnable {
	//private Variablen
	private String[] temp;
	private String[] Split = {"","","","","",""};	
	private double Z;
	private final double Pixelbreite = 0.24;
	private int Maﬂ=0;	//0=Absolutmaﬂ 1=Relativmaﬂ
	private double alpha = 0;
	private double radius = 0;
	private double beta = 0;	
	private Turtle t;
	
	//ˆffentliche Variablen
	public String[] Code;	
	public double maﬂstab;	
	public boolean FahrwegAuﬂen = true;
	public int AktuellesN = 0;
	public int AbNZeichnen = 0;
	public int BisNZeichnen = 0;
	public boolean Pause = false;
	
	public Fr‰serSimulationsEngine(String[] CodeNeu, Turtle tNeu, double maﬂstabNeu, boolean FahrwegAuﬂenNeu) {
		Code = CodeNeu;
		t = tNeu;
		maﬂstab = maﬂstabNeu;
		FahrwegAuﬂen = FahrwegAuﬂenNeu;
	}
	
	public void run() {
		t.moveto(0, 0);
		t.setForeground(Color.blue);
		while (!Thread.interrupted()) {
					
			if (AktuellesN != BisNZeichnen && !Pause) {

				for (int i=AktuellesN; i<= BisNZeichnen; i++) {	
					
					if (Thread.interrupted() | Pause) {	//wurde der Thread unterbrochen?
						break;
					} 
					
					temp = Code[i].split(" ");	//Code in einzelne Befehle splitten
					
					for (int j=0; j<temp.length; j++) {	//Befehle in Array mit definierter L‰nge ¸bernehmen;
						Split[j] = temp[j];
					}
					
					if (i==3) {	//Maﬂ feststellen
						if (Split[1].equals("G90")) {
							Maﬂ=0;
						} else if(Split[1].equals("G91")) {
							Maﬂ=1;
						}
					}
					if (i>3) {
						if (AktuellesN >= AbNZeichnen) {
							if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
								//G01 X
								if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
									if (Z<0) {
										if (Maﬂ==0) {
											t.setForeground(Color.blue);
											t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										} else if (Maﬂ==1) {
											t.setForeground(Color.blue);
											t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
										}							
									} else {
										if (Maﬂ==0) {
											if (FahrwegAuﬂen) {
												t.setForeground(Color.red);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											} else {
												t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											}
										} else if (Maﬂ==1) {
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
											t.setForeground(Color.blue);
											t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
										} else if (Maﬂ==1) {
											t.setForeground(Color.blue);
											t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
										}							
									} else {
										if (Maﬂ==0) {
											if (FahrwegAuﬂen) {
												t.setForeground(Color.red);
												t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
											} else {
												t.moveto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
											}
										} else if (Maﬂ==1) {
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

								//G01 X Y
								else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
										&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
									if (Z<0) {
										if (Maﬂ==0) {
											t.setForeground(Color.blue);
											t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
										} else if (Maﬂ==1) {
											t.setForeground(Color.blue);
											t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
										}							

									} else {
										if (Maﬂ==0) {
											if (FahrwegAuﬂen) {
												t.setForeground(Color.red);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											}
										} else if (Maﬂ==1) {
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
												t.setForeground(Color.blue);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
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
												t.setForeground(Color.blue);
												t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											} else {
												double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
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
												t.setForeground(Color.red);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
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
												t.setForeground(Color.red);
												t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY);
											} else {
												double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
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
												t.setForeground(Color.blue);
												t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
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
												t.setForeground(Color.blue);
												t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
											} else {
												double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
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
												t.setForeground(Color.red);
												t.drawto(t.turtleX, Pixel(Double.parseDouble(Split[2].substring(1))));
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[3].substring(1))-Z);
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
												t.setForeground(Color.red);
												t.drawto(t.turtleX, t.turtleY + Pixel(Double.parseDouble(Split[2].substring(1))));
											} else {
												double Faktor=-Z/Double.parseDouble(Split[3].substring(1));
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
												t.setForeground(Color.blue);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[4].substring(1))-Z);
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
												t.setForeground(Color.blue);
												t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												double Faktor=-Z/Double.parseDouble(Split[4].substring(1));
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
												t.setForeground(Color.red);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												double Faktor=-Z/(Double.parseDouble(Split[4].substring(1))-Z);
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
												t.setForeground(Color.red);
												t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												double Faktor=-Z/Double.parseDouble(Split[4].substring(1));
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
								
								
								
								
								if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
										&& istDouble(Split[3].substring(1))) {
									if (Z<0) {
										if (Maﬂ==0) {
											t.setForeground(Color.blue);
											t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
										} else if (Maﬂ==1) {
											t.setForeground(Color.blue);
											t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
										}							
										
									} else {
										if (Maﬂ==0) {
											if (FahrwegAuﬂen) {
												t.setForeground(Color.red);
												t.drawto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												t.moveto(Pixel(Double.parseDouble(Split[2].substring(1))), Pixel(Double.parseDouble(Split[3].substring(1))));
											}
										} else if (Maﬂ==1) {
											if (FahrwegAuﬂen) {
												t.setForeground(Color.red);
												t.drawto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
											} else {
												t.moveto(t.turtleX + Pixel(Double.parseDouble(Split[2].substring(1))), t.turtleY + Pixel(Double.parseDouble(Split[3].substring(1))));
											}								
										}							
									}					
								} else {
									if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1))) {
										Z=Double.parseDouble(Split[2].substring(1));
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
										double EndeX = Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1)) + Strecke(t.turtleX);
										double EndeY = Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1)) + Strecke(t.turtleY);
										Kreis(Strecke(t.turtleX), Strecke(t.turtleY), Strecke(t.turtleX) + Double.parseDouble(Split[2].substring(1)), Strecke(t.turtleY) + Double.parseDouble(Split[3].substring(1)), EndeX, EndeY, radius, false);
									}
								} 
							}
							
							else if (Split[1].equals("G90")) {
								Maﬂ=0;
							}
							
							else if (Split[1].equals("G91")) {
								Maﬂ=1;
							}
						} else {
							double Xtemp=0, Ytemp=0;
							if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
								if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
										&& istDouble(Split[3].substring(1))) {
									if (Maﬂ==1) {
										Xtemp += Double.parseDouble(Split[2].substring(1));
										Ytemp += Double.parseDouble(Split[3].substring(1));
									}				
								} else {
									if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1))) {
										if (Maﬂ == 0) {
											Z = Double.parseDouble(Split[2].substring(1));
										} 
									}
								}
							}				
							
							else if (Split[1].equals("G2") | Split[1].equals("G02")) {
								if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
										&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
										&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
										&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
									if (Maﬂ==1) {
										Xtemp += Double.parseDouble(Split[2].substring(1));
										Ytemp += Double.parseDouble(Split[3].substring(1));
									} 
								} 
							}		
							
							else if (Split[1].equals("G3") | Split[1].equals("G03")) {
								if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
										&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
										&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
										&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
									if (Maﬂ==1) {
										Xtemp += Double.parseDouble(Split[2].substring(1));
										Ytemp += Double.parseDouble(Split[3].substring(1));
									} 
								} 
							}		
							
							else if (Split[1].equals("G4") | Split[1].equals("G04")) {
								if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
										&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
									if (Maﬂ==1) {
										double radius, alpha, beta;
										
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

										Xtemp += Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
										Ytemp += Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
									}
								} 
							}
							
							else if (Split[1].equals("G5") | Split[1].equals("G05")) {
								if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
										&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
									if (Maﬂ==1) {
										double radius, alpha, beta;
										
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

										Xtemp += Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
										Ytemp += Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
									}
								} 
							} else if (Split[1].equals("G90")) {
								Maﬂ=0;
							} else if (Split[1].equals("G91")) {
								Maﬂ=1;
							}
							t.moveto(Xtemp, Ytemp);
						}
					}
						
					for (int j=0; j<Split.length; j++) {	//String Split leeren
						Split[j] = "";
					}
					AktuellesN = i;
				}
			} else {
				try {	//Thread zur Ressourcenschonung anhalten
					Thread.currentThread();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
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
	   
    private double Strecke(double pixel) {
    	double strecke = pixel * Pixelbreite / maﬂstab;
    	return strecke;
    }
       
    private double Pixel(double Strecke) {
		double pixel = Strecke * maﬂstab / Pixelbreite;
		return pixel;
	}
    
    private void Kreis(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {
    	
    	/**
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


