
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Demineur extends JFrame implements MouseListener, WindowListener, ActionListener {

    private JPanel panHaut = new JPanel();
  private JPanel panGame = new JPanel();
  private GridBagLayout layoutPanGame = new GridBagLayout();
  private Segment affMines = new Segment(); //l'afficheur du nombre de mines
  private Segment affTemps = new Segment(); //l'afficheur du temps écoulé
  private JButton btnNouveau = new JButton();
  private Border borderPan;
  private JMenuBar menu = new JMenuBar();
  private JMenu partie = new JMenu("Partie");
  private JCheckBox pause = new JCheckBox("Pause");
  private JMenuItem menuNouveau = new JMenuItem("Nouveau");
  JCheckBoxMenuItem menuDebutant = new JCheckBoxMenuItem("Débutant");
  JCheckBoxMenuItem menuIntermediaire = new JCheckBoxMenuItem("Intermédiaire");

  private BoxLayout layoutpanHaut = new BoxLayout(panHaut,
      BoxLayout.LINE_AXIS);
  private Component box2; 
  private Component box3;
  private Component box1;
  private Component box4;

  private Icon cool, oups, explosion, gagne;

  private int nbrCasesRest; 
  private int nbrMines;
  private int niveau;
  int nbrDrapeau = 0; 

  private int largeur; 
  private int longueur;

  Case[][] Game; 
  private String mines;
  private int[][] casesSelected = new int[8][2]; 
  private Temps temps = new Temps(affTemps); 

  public Demineur(int longueur, int largeur, int nbrmines, int niveau) {
      this.longueur=longueur;
      this.largeur=largeur;
      this.nbrMines=nbrmines;
      this.niveau=niveau;
      this.nbrCasesRest=this.longueur*this.largeur;
      this.Game= new Case[longueur][this.largeur];

      // on recupére les resoureces des images 
      URL location;
      location=Thread.currentThread().getContextClassLoader().getResource("./cool.gif");
      System.out.println(location);
      cool = new ImageIcon(location);
      location = Thread.currentThread().getContextClassLoader().getResource("./oups.gif");
      oups = new ImageIcon(location);
      location = Thread.currentThread().getContextClassLoader().getResource("./boum.gif");
      this.explosion = new ImageIcon(location);
      location = Thread.currentThread().getContextClassLoader().getResource("./win.gif");
      this.gagne = new ImageIcon(location);

       //création des cases
    for (int i = 0; i < this.longueur; i++) {
        for (int j = 0; j < this.largeur; j++) {
          Game[i][j] = new Case();
        }
      }

      if (this.niveau == 1) menuDebutant.setSelected(true);
      if (this.niveau  == 2) menuIntermediaire.setSelected(true);

      // maintenant on va initialiser le premier jeu 
      this.nouveau();
      
      try {
   
        Init();
        this.setVisible(true);
        btnNouveau.requestFocus();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      

  }

   //initialises le Game
   public void nouveau() {
    temps.cancel(); //Timer à 0
    btnNouveau.setIcon(cool); //Icon par défaut du bouton
    nbrDrapeau = 0;
    nbrCasesRest = this.longueur * this.largeur;
    affMines.setValeur(nbrMines);
    affTemps.setValeur(0);
    panGame.setVisible(true); 
    pause.setSelected(false);


    //Génération des mines
    //dans la chaîne, 1=mine 0=rien
    //on créé le bon nombre de mines puis on complète par des cases vides jusqu'à obtenir le nombre de cases total
    mines = "";
    for (int i = 0; i < nbrMines; i++) mines = mines + "1";
    while (mines.length() < this.longueur * this.largeur) {
      int i = (int) (Math.random() * (mines.length() + 1));
      mines = mines.substring(0, i) + "0" + mines.substring(i);
    }

    //paramètres des cases
    for (int i = 0; i < this.longueur; i++) {
      for (int j = 0; j < this.largeur; j++) {
        Game[i][j].reset();
        Game[i][j].removeMouseListener(this); //nécéssaire pour éviter un bug lors de l'appel de nouveau() une 2ème fois
        Game[i][j].addMouseListener(this); //pour les clics!!!
        if (mines.charAt(i * this.largeur + j) == '1') {
          Game[i][j].setMine(true);
        }
      }
    }
    repaint();

    //comptage pour chaque case du nombre de mines autour
    for (int i = 0; i < this.longueur; i++) {
      for (int j = 0; j < this.largeur; j++) {
        if (!Game[i][j].getMine()) {
          int n = 0;
         
          try {
            if (Game[i - 1][j - 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i - 1][j].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i - 1][j + 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i][j - 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i][j + 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i + 1][j - 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i + 1][j].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          try {
            if (Game[i + 1][j + 1].getMine()) n++;
          }
          catch (java.lang.ArrayIndexOutOfBoundsException e) {}
          Game[i][j].setChiffre(n); 
        }
      }
    }
  }


  private void Init() throws Exception {
    borderPan = BorderFactory.createEtchedBorder(Color.white,
        new Color(156, 156, 156)); // a changer 
    box2 = Box.createGlue();
    box3 = Box.createGlue();
    box1 = Box.createHorizontalStrut(8);
    box1.setSize(5, 50);
    box4 = Box.createHorizontalStrut(8);
    box4.setSize(5, 50);

    this.addWindowListener(this);

    int tailleX = this.largeur * 16 + 15; 
    int tailleY = this.longueur * 16 + 100;
    if (tailleX < 160) tailleX = 300; //taille minimum en largeur

    this.setSize(tailleX + 6, tailleY + 50 + 23 + 25); 
    this.setTitle("Démineur");
    this.setResizable(false);

    //MENU
    partie.setMnemonic('P');
    menuNouveau.addActionListener(this);
    menuNouveau.setMnemonic('N');
    menuDebutant.addActionListener(this);
    menuDebutant.setMnemonic('D');
    menuIntermediaire.addActionListener(this);
    menuIntermediaire.setMnemonic('I');
    partie.add(menuNouveau);
    partie.add(new JSeparator());
    partie.add(menuDebutant);
    partie.add(menuIntermediaire);
    menu.setBorderPainted(false);
    menu.add(partie);
    pause.setMnemonic('a');
    pause.setOpaque(false);
    pause.setFocusPainted(false);
    pause.addActionListener(this);
    menu.add(pause);
    this.setJMenuBar(menu);

    affMines.setMaximumSize(new Dimension(49, 27));
    affTemps.setMaximumSize(new Dimension(49, 27));
    btnNouveau.setMaximumSize(new Dimension(25, 25));
    btnNouveau.setMinimumSize(new Dimension(25, 25));
    panHaut.setBorder(borderPan);
    panHaut.setPreferredSize(new Dimension(450, 50));
    panHaut.setLayout(layoutpanHaut);
    panGame.setBorder(borderPan);
    panGame.setPreferredSize(new Dimension(tailleX, tailleY));
    panGame.setLayout(layoutPanGame);
    affMines.setValeur(nbrMines);
    affTemps.setValeur(0);
    btnNouveau.setPreferredSize(new Dimension(25, 25));
    btnNouveau.setFocusPainted(false);
    btnNouveau.setIcon(cool);
    btnNouveau.setMargin(new Insets(0, 0, 0, 0));
    btnNouveau.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnNouveau_actionPerformed(e);
      }
    });
    panHaut.add(box1, null);
    panHaut.add(affMines, null);
    panHaut.add(box2, null);
    panHaut.add(btnNouveau, null);
    panHaut.add(box3, null);
    panHaut.add(affTemps, null);
    panHaut.add(box4, null);
    this.getContentPane().add(panHaut, BorderLayout.NORTH);
    this.getContentPane().add(panGame, BorderLayout.CENTER);

    //gr contient les graphismes de toutes les cases
    //on le créé maintenant pour utiliser son GraphicsConfiguration()
    GraphCase gr = new GraphCase(this.getGraphicsConfiguration());

    //placement des cases dans la fenêtre
    for (int i = 0; i < this.longueur; i++) {
      for (int j = 0; j < this.largeur; j++) {
        Game[i][j].setGraphisme(gr); //on indique les graphismes à la cases
        panGame.add(Game[i][j], new GridBagConstraints(j, i, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      }
    }
  }


  public int[] caseClic(int x, int y) {
    int OFFSETX = (int) Game[0][0].getX() + 3; //décalage par rapport au coin en haut à gauche de la fenetre
    int OFFSETY = (int) Game[0][0].getY() + 22;
    int posx = -1, posy = -1;
    if (x - OFFSETX >= 0) posx = (x - OFFSETX) / 16;
    if (posx >= this.largeur) posx = -1;
    if (y - OFFSETY >= 0 && posx != -1) posy = (y - OFFSETY) / 16;
    if (posy >= this.longueur) posy = -1;
    if (posy == -1) posx = -1;
    int[] retour = {
        posx, posy};
    return retour;
  }

  public void mouseClicked(MouseEvent e) {
}

public void mousePressed(MouseEvent e) {
    try {
      int x = (int) ( (JPanel) e.getSource()).getLocation().getX() + e.getX() +
          3; //retourne une exception si on est pas au dessus d'un panneau
      int y = (int) ( (JPanel) e.getSource()).getLocation().getY() + e.getY() +
          22;
      int[] coord = caseClic(x, y); //coordonnées de la case enfoncée enregistrées dans coord
      btnNouveau.setIcon(oups); //bouton

      //si clic droit au dessus d'une case
      if (e.getButton() == e.BUTTON3 && coord[1] != -1 && coord[0] != -1) {
        int temp = Game[coord[1]][coord[0]].getEtat();
        switch (temp) {
          case 0: //affichage d'un drapeau
            Game[coord[1]][coord[0]].setEtat(2);
            nbrDrapeau++;
            affMines.setValeur(nbrMines - nbrDrapeau);
           
            break;
          case 2: //affichage d'un ?
            Game[coord[1]][coord[0]].setEtat(3);
            nbrDrapeau--;
            affMines.setValeur(nbrMines - nbrDrapeau);
          
            break;
          case 3: //RAZ
            Game[coord[1]][coord[0]].setEtat(0);
            break;
        }
        Game[coord[1]][coord[0]].repaint();
      }

      //si clic gauche, on selectionne les cases autour

      y = coord[1];
      x = coord[0];
      if (e.getButton() == e.BUTTON1 && x != -1 && y != -1 &&
          Game[y][x].getEtat() == 1 && Game[y][x].getChiffre() != 0) {
        //on enregistre les coordonnées des cases séléctionnées
        for (int i = 0; i < 7; i++) {
          for (int j = 0; j < 2; j++) {
            casesSelected[i][j] = -1; //effacement de la mémoire
          }
        }
        //essai sur les huit cases autour
        try {
          if (Game[y - 1][x - 1].getEtat() == 0) {
            Game[y - 1][x - 1].setSelected(true);
            casesSelected[0][0] = y - 1;
            casesSelected[0][1] = x - 1;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y - 1][x].getEtat() == 0) {
            Game[y - 1][x].setSelected(true);
            casesSelected[1][0] = y - 1;
            casesSelected[1][1] = x;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y - 1][x + 1].getEtat() == 0) {
            Game[y - 1][x + 1].setSelected(true);
            casesSelected[2][0] = y - 1;
            casesSelected[2][1] = x + 1;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y][x - 1].getEtat() == 0) {
            Game[y][x - 1].setSelected(true);
            casesSelected[3][0] = y;
            casesSelected[3][1] = x - 1;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y][x + 1].getEtat() == 0) {
            Game[y][x + 1].setSelected(true);
            casesSelected[4][0] = y;
            casesSelected[4][1] = x + 1;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y + 1][x - 1].getEtat() == 0) {
            Game[y + 1][x - 1].setSelected(true);
            casesSelected[5][0] = y + 1;
            casesSelected[5][1] = x - 1;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y + 1][x].getEtat() == 0) {
            Game[y + 1][x].setSelected(true);
            casesSelected[6][0] = y + 1;
            casesSelected[6][1] = x;
          }
        }
        catch (Exception exc) {}
        try {
          if (Game[y + 1][x + 1].getEtat() == 0) {
            Game[y + 1][x + 1].setSelected(true);
            casesSelected[7][0] = y + 1;
            casesSelected[7][1] = x + 1;
          }
        }
        catch (Exception exc) {}
      }
    }
    catch (java.lang.ClassCastException ex) {} //si clic n'import où
  }


  public void mouseReleased(MouseEvent e) {

    //Si c'est le premier clic du Game, on démarre le timer
    if (nbrCasesRest == this.longueur * this.largeur && e.getButton() == e.BUTTON1) {
      temps.cancel();
      temps = new Temps(affTemps);
      temps.start();
    }

    try {
      int x = (int) ( (JPanel) e.getSource()).getLocation().getX() + e.getX() +
          3; //génère des exceptions à cause du cast
      int y = (int) ( (JPanel) e.getSource()).getLocation().getY() + e.getY() +
          22;
      int[] coord = caseClic(x, y); //on récupère les coordonnées
      btnNouveau.setIcon(cool); //remise du bouton sur l'icone cool
      if (coord[0] != -1 && coord[1] != -1) { //si on est au dessus d'une case
        y = coord[1];
        x = coord[0];
        if (e.getButton() == e.BUTTON1) { //si clic gauche, on découvre
          decouvre(y, x);
          repaint();
        }
        Game[y][x].setSelected(false); //on déselctionne la case ainsi que celle de la mémoire casesSelected
        try {
          Game[casesSelected[0][0]][casesSelected[0][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[1][0]][casesSelected[1][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[2][0]][casesSelected[2][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[3][0]][casesSelected[3][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[4][0]][casesSelected[4][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[5][0]][casesSelected[5][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[6][0]][casesSelected[6][1]].setSelected(false);
        }
        catch (Exception exc) {}
        try {
          Game[casesSelected[7][0]][casesSelected[7][1]].setSelected(false);
        }
        catch (Exception exc) {}
      }
    }
    catch (java.lang.ClassCastException ex) {} //Si le clic n'est pas au dessus d'un panneau
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
}

 //déclenchée par un appuie sur le bouton
 void btnNouveau_actionPerformed(ActionEvent e) {
  if (!pause.isSelected()) nouveau();
  }

//méthode pour découvrir les cases
public void decouvre(int y, int x) {
    //Si la case est normale ou avec un ?
    if ( (Game[y][x].getEtat() == 0 || Game[y][x].getEtat() == 3) &&
        !Game[y][x].getMine()) {
      nbrCasesRest--; //nombre de cases non découvertes
      Game[y][x].setEtat(1); //on indique que la case est découverte
      if (Game[y][x].getChiffre() == 0) { // Si le nombre de mines autour est nul, on découvre les cases autour
        floodfill1(x - 1, y - 1);
        floodfill1(x - 1, y);
        floodfill1(x - 1, y + 1);
        floodfill1(x, y - 1);
        floodfill1(x, y + 1);
        floodfill1(x + 1, y - 1);
        floodfill1(x + 1, y);
        floodfill1(x + 1, y + 1);
      }
    }

    //Si on est au dessus d'un chiffre
    else if (Game[y][x].getEtat() == 1 && Game[y][x].getChiffre() != 0) {
      int n = 0; //on compte le nombre de drapeaux placés
      if (floodfill2(x - 1, y - 1)) n++;
      if (floodfill2(x - 1, y)) n++;
      if (floodfill2(x - 1, y + 1)) n++;
      if (floodfill2(x, y - 1)) n++;
      if (floodfill2(x, y + 1)) n++;
      if (floodfill2(x + 1, y - 1)) n++;
      if (floodfill2(x + 1, y)) n++;
      if (floodfill2(x + 1, y + 1)) n++;

      if (n == Game[y][x].getChiffre()) { //si il y en a autant que le nombre de mines autour, on découvre les 8 cases autour par un appel récursif de decouvre(int, int)
        if (floodfill3(x - 1, y - 1)) decouvre(y - 1, x - 1);
        if (floodfill3(x - 1, y)) decouvre(y, x - 1);
        if (floodfill3(x - 1, y + 1)) decouvre(y + 1, x - 1);
        if (floodfill3(x, y - 1)) decouvre(y - 1, x);
        if (floodfill3(x, y + 1)) decouvre(y + 1, x);
        if (floodfill3(x + 1, y - 1)) decouvre(y - 1, x + 1);
        if (floodfill3(x + 1, y)) decouvre(y, x + 1);
        if (floodfill3(x + 1, y + 1)) decouvre(y + 1, x + 1);
      }
    }

    //Si on clique sur une mine
    else if ( (Game[y][x].getEtat() == 0 || Game[y][x].getEtat() == 3) &&
             Game[y][x].getMine()) {
      temps.cancel(); //fin du timer
      Game[y][x].setEtat(4); //boum
      btnNouveau.setIcon(explosion);
      JOptionPane.showMessageDialog(null,
                    "vous avez perdu !!!", "perdu",
                    JOptionPane.ERROR_MESSAGE);
      for (int i = 0; i < this.longueur; i++) {
        for (int j = 0; j < this.largeur; j++) {
          Game[i][j].removeMouseListener(this); //on bloque les cases
          Game[i][j].setBlocked(true);
          if (! (y == i && x == j) && mines.charAt(i * this.largeur + j) == '1' &&
              Game[i][j].getEtat() != 2)

          
            Game[i][j].setEtat(5); //on l' affiche
        }
      }
    
      for (int i = 0; i < this.longueur; i++) {
        for (int j = 0; j < this.largeur; j++) {
          if (Game[i][j].getEtat() == 2 && !Game[i][j].getMine()) Game[i][j].
              setEtat(6);
        }
      }
    }

    if (nbrCasesRest == nbrMines && !Game[0][0].estBloque()) {
      temps.cancel(); //fin du timer
      affMines.setValeur(0);
      btnNouveau.setIcon(gagne);
      JOptionPane.showMessageDialog(null,
                    "vous avez gagné !!", "gagné",
                    JOptionPane.INFORMATION_MESSAGE);
      
      for (int i = 0; i < this.longueur; i++) {
        for (int j = 0; j < this.largeur; j++) {
          Game[i][j].removeMouseListener(this); 
          Game[i][j].setBlocked(true);
          if (Game[i][j].getMine()) Game[i][j].setEtat(2); 
        }
      }
    }
  }


// algo floodfill 1
 //si la case existe, on la découvre et si necessaire, on appelle le découvrement des cases autour
 public void floodfill1(int x, int y) {
    if (x >= 0 && y >= 0 && x < this.largeur && y < this.longueur) {
      if (Game[y][x].getEtat() == 0 && Game[y][x].getChiffre() != 0) {
        Game[y][x].setEtat(1);
        nbrCasesRest--;
      }
      if (Game[y][x].getEtat() == 0 && Game[y][x].getChiffre() == 0)
        decouvre(y, x); //Si le nombre de mines autour est nul, on découvre les cases autour
    }
  }

   //vérifie si la case existe et si elle porte un drapeau
   public boolean floodfill2(int x, int y) {
    if (x >= 0 && y >= 0 && x < this.largeur && y < this.longueur) {
      if (Game[y][x].getEtat() == 2)
        return true;
    }
    return false;
  }
  //vérifie si la case existe et si elle n'est pas découverte ou si elle porte un '?'
  public boolean floodfill3(int x, int y) {
    if (x >= 0 && y >= 0 && x < this.largeur && y < this.longueur) {
      if (Game[y][x].getEtat() == 0 || Game[y][x].getEtat() == 3)
        return true;
    }
    return false;
  }


  public void windowOpened(WindowEvent e) {
}

public void windowClosing(WindowEvent e) {
  temps.stop();
  System.exit(0);
}

public void windowClosed(WindowEvent e) {
}

public void windowIconified(WindowEvent e) {
  try {
    temps.suspend();
  } 
  catch (Exception esc) {}
}

public void windowDeiconified(WindowEvent e) {
    try {
      temps.resume();
    } 
    catch (Exception esc) {}
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

   //évenements liés au menu
   public void actionPerformed(ActionEvent e) {
    if (e.getSource() == menuNouveau) nouveau();
    else if (e.getSource() == menuDebutant && this.niveau != 1) {
      this.dispose(); // on détruit la fenetre
      System.gc();
      if (this.niveau == 1) menuDebutant.setSelected(true);
      Demineur demineur = new Demineur(8, 8, 10, 1); 
    }
    else if (e.getSource() == menuDebutant && !menuDebutant.isSelected())
      menuDebutant.setSelected(true);
    else if (e.getSource() == menuIntermediaire && this.niveau != 2) {
      this.dispose(); // on détruit la fenetre
      System.gc();
      if (this.niveau == 2) menuIntermediaire.setSelected(true);
      Demineur demineur = new Demineur(16, 16, 40, 2);
    }
    else if (e.getSource() == menuIntermediaire &&
             !menuIntermediaire.isSelected()) menuIntermediaire.setSelected(true);
    
   
  }

}// fin
