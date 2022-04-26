import java.util.*;
import javax.swing.*;


public class Main {

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new Demineur(8,8,10 ,1);
  }
}