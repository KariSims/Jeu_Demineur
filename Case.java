
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//La classe qui gére les  evénements de la souris


public class Case extends JPanel  implements MouseListener {


  private int etat = 0; 
  private boolean mine = false;
  private boolean enfonce = false; 
  private boolean bloque = false;
  private int chiffre = 0; 

  private GraphCase gr  = null; 

  // voici le constructeur 

  public Case() {
    try {
      //construction de la case
      Initialisation();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
 
  private void Initialisation() throws Exception {
    this.setBackground(GraphCase.dessus);// background des cases 
    this.setMaximumSize(new Dimension(16, 16)); 
    this.setMinimumSize(new Dimension(16, 16));
    this.addMouseListener(this);
    this.setPreferredSize(new Dimension(16, 16));

  }

  //ici on implémente lzs fonctions abstraites de MouseListener => les evenements  de la souris 
// si on clique 
  public void mouseClicked(MouseEvent e) {
}


// sin on enfonce la case 
  public void mousePressed(MouseEvent e) {
    //Selectionne la case si on clique dessus
    if (e.getModifiersEx() == 16 && etat != 1 && etat != 2 && !bloque) {
      this.enfonce = true;
      repaint();
    }
  }

  // la focntion qui déselectionne une case 
  public void mouseReleased(MouseEvent e) {
    
    enfonce = false;
    repaint();
  }

  public void mouseEntered(MouseEvent e) {
    //Si la case est relev�e est que la souris passe dessus avec le clic gauche, on s�l�ctionne
    if (e.getModifiersEx() == 16 && etat != 1 && etat != 2 && !bloque) {
      enfonce = true;
      repaint();
    }
  }

  public void mouseExited(MouseEvent e) {

    enfonce = false;
    repaint();
  }

  // verfier si c miné ou non
  public boolean getMine() {
    return this.mine;
  } 

  public void setMine(boolean mine) {
    this.mine = mine;
  }

  public int getEtat() {
    return etat;
  }

  public void setEtat(int etat) {
    this.etat = etat;
  }

  public int getChiffre() {
    return chiffre;
  }

  public void setChiffre(int chiffre) {
    this.chiffre = chiffre;
  }

  public boolean isSelected() {
    return this.enfonce;
  }

  public void setSelected(boolean selected) {
    this.enfonce = selected;
    this.paintComponent(this.getGraphics());
  }

  public void setBlocked(boolean blocked) {
    this.bloque = blocked;
  }

  public boolean estBloque() {
    return this.bloque;
  }

  public void setGraphisme(GraphCase gr) {
    this.gr = gr;
  }

  // fonction pour réinitialiser la case 
  public void reset() { 
    this.etat = 0;
    this.enfonce = false;
    setMine(false);
    setBlocked(false);
  }

    public void paintComponent(Graphics g) {
    super.paintComponent(g);
  
    if (gr != null) {
      if (!enfonce) { //case non enfoncée
        if (etat == 0) { //normal
          g.setColor(Color.white); //bordure haut et gauche blanche
          g.drawLine(0, 0, 0, 15);
          g.drawLine(0, 0, 15, 0);
        }
        else if (etat == 1) g.drawImage(GraphCase.chiffre[chiffre], 0, 0, null); 
        else if (etat == 2) g.drawImage(GraphCase.drapeau, 0, 0, null); 
        else if (etat == 6) g.drawImage(GraphCase.erreur, 0, 0, null); 
        else if (etat == 3) g.drawImage(GraphCase.question, 0, 0, null);
        else if (etat == 4) g.drawImage(GraphCase.boum, 0, 0, null); 
        else if (etat == 5) g.drawImage(GraphCase.mine, 0, 0, null);
      }
      else { 
        if (etat == 3) g.drawImage(GraphCase.questionSel, 0, 0, null); //?
        else if (etat != 1) { 
          g.setColor(Color.gray); 
          g.drawLine(0, 0, 0, 15);
          g.drawLine(0, 0, 15, 0);
        }
      }
    }

    g.setColor(Color.darkGray); 
    g.drawLine(0, 15, 15, 15);
    g.drawLine(15, 0, 15, 15);
    g.dispose();
  }


}
