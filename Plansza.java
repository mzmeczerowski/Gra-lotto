import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


//model gry
class Gra implements Serializable{
	int tab[][] = new int[12][12];
	int start_tab[][] = new int[12][12];
	int roz_plansza, poziom;
	ArrayList<Punkt> pamiec = new ArrayList<Punkt>();
	ArrayList<Punkt> cofanie_pam = new ArrayList<Punkt>();
	ArrayList<Punkt> rozwiazanie = new ArrayList<Punkt>();
	
	class Punkt
	{
		int x;
		int y;
		Punkt () {}
		Punkt (int x, int y) {this.x = x; this.y = y;}
	}
	
	int zliczaj(int tab[][],int i, int j)
	{
		if (i < 0 || j < 0 || i >= roz_plansza || j >= roz_plansza || tab[i][j] == 0)
			return 0;
		else
		{
			tab[i][j]=0;
			
//			try { System.in.read(); } catch (IOException ex) { }
			return 1 + zliczaj(tab, i+1, j) + zliczaj(tab, i, j+1) + zliczaj(tab, i-1, j) + zliczaj(tab, i, j-1);
		}
	}
	
	Boolean czyDzieli(int tab[][], int n)
	{
		int tab_tmp[][] = new int[roz_plansza][roz_plansza];
		int licznik = 0;
		//Kopiowanie tabeli
		for (int i=0; i<roz_plansza; i++)
			for (int j=0; j<roz_plansza; j++)
				tab_tmp[i][j] = tab[i][j];
		

			licznik = zliczaj(tab_tmp, 0, 0);
			if (licznik == 0)
				licznik = zliczaj(tab_tmp, 0, 1);

			System.out.println(licznik);
		
		if (licznik + n + 1 == roz_plansza*roz_plansza)
			return false;
		else
			return true;
	}
	
	Gra (int roz_plansza, int poziom )
	{
		this.roz_plansza = roz_plansza;
		this.poziom = poziom;
		pamiec.clear();
		cofanie_pam.clear();
		
		Random generator = new Random();
		int hard_check = 0, x;
		//generowanie planszy wraz z inicjacją gry, po ustaleniu poziomu trudności i rozmiaru planszy.
		for (int i=0; i < roz_plansza; i++)
			for (int j=0; j < roz_plansza; j++)
			{
				x = generator.nextInt(1100);
				int h=0, l, k;
				tab[i][j] = (x+h)%roz_plansza+1;
				//Faza budowania pełnej tabeli.
				int check = 1;
				while(check != 0)
				{
					for( l=i-1; l>=0; l--)
					{
						if (tab[i][j] == tab[l][j])
						{
							h++;
							tab[i][j] = (x+h)%roz_plansza+1;
							l=i;
						}
					}
					
					for(k=j-1; k>=0 ; k--)
					{
						
						
						if (tab[i][j] == tab[i][k])
						{
							h++;
							tab[i][j] = (x+h)%roz_plansza+1;
							k=j;
							l=i;
							check++;
						}
							
						if (h > roz_plansza)
						{
							j=1;
							check = 1;
							hard_check++;
							if (hard_check >10)
								i--;
							break;
						}
						
					}
					check--;
				}					
				start_tab[i][j] = tab[i][j];
			}
		//Faza dobierania podwójnych liczb.
		ArrayList<Punkt> dublet = new ArrayList<Punkt>();
		Punkt p = new Punkt();
		
		for (int n = 0; n < roz_plansza*poziom*2/3; n++)
		{
			//sprawdzanie czy Punkt juz nie został wylosowany.
			Boolean check0 = false;
			do
			{
				check0 = false;
				x = generator.nextInt(roz_plansza*roz_plansza);
				p = new Punkt(x/roz_plansza, x%roz_plansza);
				
				//sprawdzanie czy Punkt juz nie został wylosowany.
				for (int m = dublet.size(); m > 0; m--)
				{
					if ( p.x == dublet.get(m-1).x && p.y == dublet.get(m-1).y)
					{
						System.out.println("Podwójny: (" + p.x + ", " + p.y + ")");
						check0 = true;
						break;
					}
				} 
				if (check0) {continue;}
				
				//sprawdzenie czy nie leżą obok siebie.
				
				for (int m = dublet.size(); m >0; m--)
				{
					if ( 
							(p.x == dublet.get(m-1).x && p.y == dublet.get(m-1).y+1) ||
							(p.x == dublet.get(m-1).x && p.y == dublet.get(m-1).y-1) ||
							(p.x == dublet.get(m-1).x+1 && p.y == dublet.get(m-1).y) ||
							(p.x == dublet.get(m-1).x-1 && p.y == dublet.get(m-1).y)
						)
					{
						System.out.println("Obok siebie: (" + p.x + ", " + p.y + ")");
						check0 = true;
						break;
					}
				} 
				if (check0) {continue;}
				
				int tmp = tab[p.x][p.y];
				tab[p.x][p.y] = 0; 
				//sprawdzenie czy przekatnie stykajace sie pola, nie dzielą planszy.

				if ( czyDzieli(tab, n))
				{
					System.out.println("dzieli obszar: (" + p.x + ", " + p.y + ")");
					check0 = true;
					tab[p.x][p.y] = tmp; 
					continue;
				}
			} while (check0);
			
			//przypisanie ostateczne
			dublet.add(p);
			System.out.println("i = " + p.x + ", j = " + p.y);
			tab[p.x][p.y] = 0;
		}
		
		for (int i=0; i < dublet.size(); i++)
		{
			rozwiazanie.add(dublet.get(i));
			tab[dublet.get(i).x][dublet.get(i).y] = (start_tab[dublet.get(i).x][dublet.get(i).y] +1)%roz_plansza +1;
			start_tab[dublet.get(i).x][dublet.get(i).y] = tab[dublet.get(i).x][dublet.get(i).y];
		}
		
	}
	
	
	int getValue (int i, int j) {return tab[i][j];}
	
	void setNull (int i, int j) {tab[i][j] = 0;}
	
	void backValue (int i, int j) {tab[i][j] = start_tab[i][j];}
	
	Boolean SprawdzCzyKoniec ()
	{
		for (int k=0; k < 8; k++) {
			for (int i = 0; i < 8; i++)
				for (int j = i+1; j < 8 ; j++)
				{
					if (tab[k][i] != 0 && tab[k][j] != 0)
						if (tab[k][i] == tab[k][j])
							return false;
					
					if ( tab[i][k] != 0 && tab[j][k] != 0)
						if ( tab[i][k] == tab[j][k] )
							return false;
				}
		}
		return true;
	}
	
	Boolean SprawdzCzyZgodnieZRegolami (int i, int j)
	{
		//próbne wstawienie wyboru do tabelki;
		tab[i][j] = 0;

		ArrayList<Punkt> wybrane_pola = new ArrayList<Punkt>();
		Punkt p = new Punkt(i,j);
		Boolean check_point = true;
		
		//wstawienie zaznaczeń do ArrayList:
		for (int m = 0; m < roz_plansza; m++)
			for (int n=0; n< roz_plansza; n++)
				if (tab[m][n] == 0)
					wybrane_pola.add(new Punkt(m, n));
		
		//sprawdzenie czy nie leżą obok siebie.
		for (int m = wybrane_pola.size(); m >0; m--)
		{
			if ( 
					(p.x == wybrane_pola.get(m-1).x && p.y == wybrane_pola.get(m-1).y+1) ||
					(p.x == wybrane_pola.get(m-1).x && p.y == wybrane_pola.get(m-1).y-1) ||
					(p.x == wybrane_pola.get(m-1).x+1 && p.y == wybrane_pola.get(m-1).y) ||
					(p.x == wybrane_pola.get(m-1).x-1 && p.y == wybrane_pola.get(m-1).y)
				)
			{
				System.out.println("Obok siebie: (" + p.x + ", " + p.y + ")");
				check_point = false;
				break;
			}
		} 
		
		//sprawdzenie czy przekatnie stykajace sie pola, nie dzielą planszy.
		if ( czyDzieli(tab, wybrane_pola.size()-1))
		{
			System.out.println("dzieli obszar: (" + p.x + ", " + p.y + ")");
			check_point = false;
		}

		
		//przywrucenie przed stan proby
		if (!check_point)
			tab[i][j] = start_tab[i][j];

		return check_point;
	}
}


class Plansza extends JFrame {
	int roz_plansza=12, poziom;
	JButton tab[][] = new JButton[roz_plansza][roz_plansza];
  Gra gra;
  JPanel plansza = new JPanel();
  JPanel sterowanie = new JPanel();
  JPanel start = new JPanel();
  JTextField t = new JTextField(10);
  JCheckBox
	level1 = new JCheckBox("level 1"),
	level2 = new JCheckBox("level 2"),
	level3 = new JCheckBox("level 3");
  JButton
  sprawdz = new JButton("sprawdz"),
  wstecz = new JButton("<-- wstecz"),
  wprzod = new JButton("wprzód -->"),
  help = new JButton("mini Help"),
  nowa = new JButton("NOWA GRA"),
  zapisz = new JButton("zapisz gre"),
  odczytaj = new JButton("wczytaj gre");
  
  public Plansza(int rozmiar, int poziom) {
	this.roz_plansza = rozmiar;
	this.poziom = poziom;
	gra = new Gra(roz_plansza, poziom);
    int i,j;
    Container cp = getContentPane();
    cp.setLayout(new GridLayout(1,3));
    cp.add(start);
	start.add(level1);
	start.add(level2);
	start.add(level3);
	level1.addActionListener(new L1());
	level2.addActionListener(new L2());
	level3.addActionListener(new L3());
    
    cp.add(plansza); cp.add(sterowanie);
    sterowanie.setLayout(new GridLayout(8,1));
    sterowanie.add(t);
    sterowanie.add(sprawdz);
    sterowanie.add(wstecz);
    sterowanie.add(wprzod);
    sterowanie.add(zapisz);
    sterowanie.add(odczytaj);
    sterowanie.add(help);
    sterowanie.add(nowa);
    sprawdz.addActionListener(new SPRAWDZ());
    wstecz.addActionListener(new WSTECZ());
    wprzod.addActionListener(new WPRZOD());
    zapisz.addActionListener(new ZAPISZ());
    odczytaj.addActionListener(new ODCZYTAJ());
    help.addActionListener(new HELP());
    nowa.addActionListener(new NOWA());
    t.setFont(t.getFont().deriveFont(30.0f));
    sprawdz.setFont(sprawdz.getFont().deriveFont(30.0f));
    wstecz.setFont(sprawdz.getFont().deriveFont(30.0f));
    wprzod.setFont(sprawdz.getFont().deriveFont(30.0f));
    zapisz.setFont(sprawdz.getFont().deriveFont(30.0f));
    odczytaj.setFont(sprawdz.getFont().deriveFont(30.0f));
    help.setFont(sprawdz.getFont().deriveFont(30.0f));
    nowa.setFont(sprawdz.getFont().deriveFont(30.0f));
    wstecz.setEnabled(false);
    wprzod.setEnabled(false);
    plansza.setLayout(new GridLayout(roz_plansza,roz_plansza));
    for (i=0;i<roz_plansza;i++)
       for (j=0;j<roz_plansza;j++)
       {
    	   tab[i][j]=new JButton(Integer.toString(gra.getValue(i, j)));
    	   tab[i][j].setFont(new Font("Arial", 1, 20));
    	   plansza.add(tab[i][j]);
    	   (tab[i][j]).addActionListener(new B(i,j));
    	   if (gra.getValue(i, j) == 0)
    		   tab[i][j].setBackground(Color.red);
       }
 
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  class B implements ActionListener {
    int i,j;
    B(int i,int j) {this.i=i;this.j=j;}  
      public void actionPerformed(ActionEvent e)
      {
    	  if (gra.getValue(i, j) != 0)
    	  {
    		  if (gra.SprawdzCzyZgodnieZRegolami(i, j))
    		  {
    			  gra.pamiec.add(gra.new Punkt(i, j));
		    	  tab[i][j].setText(null);
		    	  tab[i][j].setBackground(Color.black);
		    	  gra.setNull(i,j);
		    	  wstecz.setEnabled(true);
		    	  
		    	  gra.cofanie_pam.clear();
		    	  wprzod.setEnabled(false);
    		  }
    	  }
    	  else
    	  {
    		  gra.pamiec.add(gra.new Punkt(i,j));
    		  gra.backValue(i,j);
    		  tab[i][j].setText(Integer.toString(gra.start_tab[i][j]));
    		  tab[i][j].setBackground(new JButton().getBackground());
    		  wstecz.setEnabled(true);
    	  }
      }
  }
  
  class SPRAWDZ implements ActionListener {
	    int i,j;
	    SPRAWDZ() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  if (gra.SprawdzCzyKoniec())
	    		  t.setText("Gratulacje udało Ci sie!!!");
	    	  else 
	    		  t.setText("Niestety, Próbuj dalej");
	    		  
	      }
	  }
  
  class WSTECZ implements ActionListener {
	    int i,j;
	    WSTECZ() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  gra.cofanie_pam.add(gra.pamiec.get((gra.pamiec.size()-1)));
	    	  i = gra.pamiec.get(gra.pamiec.size()-1).x;
	    	  j = gra.pamiec.get(gra.pamiec.size()-1).y;
	    	  
	    	  if (gra.getValue(i, j) != 0)
	    	  {
			    	  tab[i][j].setText(null);
			    	  tab[i][j].setBackground(Color.black);
			    	  gra.setNull(i,j);  
	    	  }
	    	  else
	    	  {
	    		  gra.backValue(i,j);
	    		  tab[i][j].setText(Integer.toString(gra.start_tab[i][j]));
	    		  tab[i][j].setBackground(new JButton().getBackground());
	    	  }
	    	  
	    	  wprzod.setEnabled(true);
	    	  gra.pamiec.remove(gra.pamiec.size()-1);
	    	  if (gra.pamiec.isEmpty())
	    		  wstecz.setEnabled(false);
	      }
	  }
  
  class WPRZOD implements ActionListener {
	    int i,j;
	    WPRZOD() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  gra.pamiec.add(gra.cofanie_pam.get((gra.cofanie_pam.size()-1)));
	    	  i = gra.cofanie_pam.get(gra.cofanie_pam.size()-1).x;
	    	  j = gra.cofanie_pam.get(gra.cofanie_pam.size()-1).y;
	    	  
	    	  if (gra.getValue(i, j) != 0)
	    	  {
	    		  if (gra.SprawdzCzyZgodnieZRegolami(i, j))
	    		  {
	    			  
			    	  tab[i][j].setText(null);
			    	  tab[i][j].setBackground(Color.black);
			    	  gra.setNull(i,j);
			    	  
	    		  }
	    	  }
	    	  else
	    	  {
	    		  gra.backValue(i,j);
	    		  tab[i][j].setText(Integer.toString(gra.start_tab[i][j]));
	    		  tab[i][j].setBackground(new JButton().getBackground());
	    	  }
	    	  wstecz.setEnabled(true);
	    	  gra.cofanie_pam.remove(gra.cofanie_pam.size()-1);
	    	  if (gra.cofanie_pam.isEmpty())
	    		  wprzod.setEnabled(false);
	      }
	  }
  
  class ZAPISZ implements ActionListener {
	    int i,j;
	    ZAPISZ() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  try{
		    	   FileOutputStream f = new FileOutputStream("SerialData");
		    	   ObjectOutputStream os = new ObjectOutputStream(f);
		    	   os.writeObject(gra);
		    	       f.close();
		    	} catch (IOException e1){}
	      }
	  }
  
  class ODCZYTAJ implements ActionListener {
	    int i,j;
	    ODCZYTAJ() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  try{
	  		    ObjectInputStream is = new ObjectInputStream(new FileInputStream("SerialData"));
	  		    gra = (Gra)is.readObject();
		    	  for (i=0;i<roz_plansza;i++)
		    	       for (j=0;j<roz_plansza;j++)
		    	       {
		    	    	   tab[i][j].setText(Integer.toString(gra.getValue(i, j)));
		    	  			tab[i][j].setBackground(new JButton().getBackground());
		    	  			wstecz.setEnabled(false);
		    	  			wprzod.setEnabled(false);
		    	  			t.setText("");
		    	  			if (gra.getValue(i, j) == 0)
		    	     		   tab[i][j].setBackground(Color.red);
		    	       }	    	    	   
		      
	  	            is.close();
	  		} catch (IOException e1){System.out.println("--wyjatek!");}
	  	  	  catch (ClassNotFoundException e1){}
	      }
	  }
  
  class HELP implements ActionListener {
	    int i,j;
	    HELP() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  if (!gra.rozwiazanie.isEmpty())
	    	  {
		    	  B b = new B(gra.rozwiazanie.get(gra.rozwiazanie.size()-1).x, gra.rozwiazanie.get(gra.rozwiazanie.size()-1).y);
		    	  gra.rozwiazanie.remove(gra.rozwiazanie.size()-1);
		    	  b.actionPerformed(e);
	    	  }
	      }
	  }
  
  class NOWA implements ActionListener {
	    int i,j;
	    NOWA() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  gra = new Gra(roz_plansza, poziom);
	    	  for (i=0;i<roz_plansza;i++)
	    	       for (j=0;j<roz_plansza;j++)
	    	       {
	    	    	   tab[i][j].setText(Integer.toString(gra.getValue(i, j)));
	    	  			tab[i][j].setBackground(new JButton().getBackground());
	    	  			wstecz.setEnabled(false);
	    	  			wprzod.setEnabled(false);
	    	  			t.setText("");
	    	  			if (gra.getValue(i, j) == 0)
	    	     		   tab[i][j].setBackground(Color.red);
	    	       }	    	    	   
	      }
	  }
  

class L1 implements ActionListener {
	    L1() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  poziom = 1;
	    	  level2.setSelected(false);
	    	  level3.setSelected(false);
	      }
	  }

class L2 implements ActionListener {
	    L2() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  poziom = 2;
	    	  level1.setSelected(false);
	    	  level3.setSelected(false);
	      }
	  }

class L3 implements ActionListener {
	    L3() {}  
	      public void actionPerformed(ActionEvent e)
	      {
	    	  poziom = 3;
	    	  level1.setSelected(false);
	    	  level2.setSelected(false);
	      }
	  }

 public static void main(String[] args) 
 {  
	  	JFrame f = new Plansza(8, 1);
	    f.setSize(1200,420);
	    f.setLocation(100,100);
	    f.setVisible(true);
	 
    
 }
} 