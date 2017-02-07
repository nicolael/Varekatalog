/*
* Verktøy for å lage Produktkatalog
* @author (Nicolas Esteban Lopez)
* version (Friday 24.april 2015)
*/
import java.awt.*;
import javax.swing.*;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream; 
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.*;
import javax.swing.border.*;

import javax.swing.Action;
import javax.swing.table.TableCellRenderer;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.JScrollBar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Element;
import com.itextpdf.text.Chunk; 

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;


class ProduktKatalog{
	public static void main(String[]args){

		
		final Vindu window = new Vindu();

		window.setVisible(true);

		window.addWindowListener(new WindowAdapter() { //Lagrer data til fil når vi krysser ut vinduet
			public void windowClosing(WindowEvent e)
			{
				JOptionPane.showMessageDialog(window,"Programmet vil nå bli avsluttet, all data vil bli lagret"
					,"Lagre data",JOptionPane.PLAIN_MESSAGE);

				window.skrivTilFil();
			
				System.exit(0);
			}
		});

	}//end of main
}//end of ProduktKatalog class
class RegListe implements Serializable{ //Klassen som inneholder metoder for administrere lista
	ArrayList<Produkt> produktlista = new ArrayList<Produkt>(); //Elementene blir lagt til her

    public void settInn(String produktnr,String beskrivelse,String kategori){
    	Produkt produkt = new Produkt(produktnr,beskrivelse,kategori);
    	produktlista.add(produkt);
    }
    public Produkt sok(String produktnr){ //kanskje en returnere en arraylist(String) med alle relevante resultater
    									 //Senere ta storrelse på den med .Size(). og så loope og sette inn i data[][] linje 264

    	for(Produkt prod : produktlista){
    		String varenr = produktnr.replaceAll(" ", "");
    		if(prod.get_produktNr().equals(varenr)){
    			return prod;
    		}
    	}

    	return null;
    }
    public ArrayList<Produkt> sokResult(String sokeTxt){
    	
    	ArrayList<Produkt> results = new ArrayList<Produkt>();
    	sokeTxt=sokeTxt.toLowerCase();

    	for(Produkt prod : produktlista){
    		if(prod.get_beskrivelse().toLowerCase().contains(sokeTxt)){
    				results.add(prod);
    		}
    	}
    	if(results.size()>=0){
    		return results;
    	}
    	return null;

    }
    public ArrayList<Produkt> traverser(String sokeTxt){
    	Iterator<Produkt> iterator = produktlista.iterator();
    	ArrayList<Produkt> results = new ArrayList<Produkt>();
		sokeTxt=sokeTxt.toLowerCase();

		while (iterator.hasNext()) {
			Produkt p = iterator.next();
			if(p.get_beskrivelse().toLowerCase().contains(sokeTxt)){
				results.add(p);
			}
		}
		return results;
    }

	public void read_file(String path){
		File file = new File(path);
		int count_lines = 0;
		try{
			Scanner input = new Scanner(file);
			input.nextLine();

			while(input.hasNextLine()){
				String line = input.nextLine();
				String lines[]=line.split(",");
				settInn(lines[0],lines[1],lines[5]);	
				count_lines++;								
			}
			input.close();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public ArrayList<Produkt> getList(){
		return produktlista;
	}
	public void create_pdf(){
		Document document = new Document(PageSize.A4);
		String no_img_path = "."+File.separator+"Bilder"+File.separator+"img_missing.png";
		String dir= System.getProperty("user.home");
		String dir_pdf = dir+File.separator+"Produkt_Katalog.pdf";

		PdfWriter pdfWriter=null;

		try
		{ 

		pdfWriter.getInstance(document, new FileOutputStream(dir_pdf));
    	document.open();
    	PdfPTable pdfPTable = new PdfPTable(3);

    	pdfPTable.setWidthPercentage(100);
    	pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
      	pdfPTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

    	float[] columnWidths = {1f, 2.5f, 2f};

		pdfPTable.setWidths(columnWidths);

    	PdfPCell pdfCell = new PdfPCell(new Phrase("Varenummer"));
    	pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      	pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	pdfCell.setBackgroundColor(BaseColor.YELLOW);
    	//pdfCell.setFixedHeight(72f);
        pdfPTable.addCell(pdfCell);

        pdfCell = new PdfPCell(new Phrase("Beskrivelse"));
        pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      	pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	pdfCell.setBackgroundColor(BaseColor.YELLOW);
    	//pdfCell.setFixedHeight(72f);
    	pdfPTable.addCell(pdfCell);

    	/*
    	pdfCell = new PdfPCell(new Phrase("kategori"));
    	pdfCell.setBackgroundColor(BaseColor.YELLOW);
    	pdfPTable.addCell(pdfCell);
    	*/
     	
    	pdfCell = new PdfPCell(new Phrase("Bilde"));
    	pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      	pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	pdfCell.setBackgroundColor(BaseColor.YELLOW);
    	pdfPTable.addCell(pdfCell);
    

    	for(Produkt p : produktlista){
    		pdfPTable.addCell(p.get_produktNr());
   			pdfPTable.addCell(p.get_beskrivelse());
   			//pdfPTable.addCell(p.get_kategori());
   			try{

   				if(p.get_path()!=null){


   					BufferedImage bi = new BufferedImage(
				    p.get_normalSize().getIconWidth(),
				    p.get_normalSize().getIconHeight(),
				    BufferedImage.TYPE_INT_RGB);
					Graphics g = bi.createGraphics();
					// paint the Icon to the BufferedImage.
					p.get_normalSize().paintIcon(null, g, 0,0);
   					Image img = Image.getInstance(bi,null);
   					//Image img = Image.getInstance(p.get_path());
   					pdfCell = new PdfPCell(img,true);
   					pdfCell.setPadding(2);
   					pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
   					pdfCell.setBackgroundColor(BaseColor.WHITE);	
    				pdfCell.setFixedHeight(150f);
    				//add the cell into the table
    				pdfPTable.addCell(pdfCell);
    				g.dispose();

   				}else{
    				//pdfPTable.addCell(Image.getInstance(no_img_path));
    				Image img = Image.getInstance(no_img_path);
    				pdfCell = new PdfPCell(img,true);
    				pdfCell.setBackgroundColor(BaseColor.WHITE);
    				pdfCell.setFixedHeight(150f);
    				//add the cell into the table
    				pdfPTable.addCell(pdfCell);
    	
    			}

			}catch(IOException ioe)
			{
				ioe.printStackTrace();

			}
		
    	}
    
        document.add(pdfPTable);
    	document.close();

    	}catch (DocumentException err) {
           	err.printStackTrace();
        } catch (FileNotFoundException error) {
            error.printStackTrace();
        }
	}
}//end of RegListe class
class Produkt implements Serializable{
	private String produktnr;
	private String beskrivelse;
	private String kategori;
	private ImageIcon img;
	private ImageIcon normal_size;
	String path = "."+File.separator+"Bilder"+File.separator+"img_missing.png";
	private String img_path=path;
	private Image test;
	
	Produkt(String produktnr, String beskrivelse,String kategori){
		this.produktnr = produktnr;
		this.beskrivelse = beskrivelse;
		this.kategori = kategori;
	}//end of contructor

	String get_produktNr(){
		return produktnr;
	}
	String get_beskrivelse(){
		return beskrivelse;
	}
	String get_kategori(){
		return kategori;
	}
	public String toString(){
		String s = produktnr + " " + " " + beskrivelse + " " + kategori;
		return s;
	}

	public void set_image(String path){


		try{
			normal_size = new ImageIcon(path);
			BufferedImage image = ImageIO.read(new File(path));
    		BufferedImage new_img= new BufferedImage(275,200,BufferedImage.TYPE_INT_RGB);
    		new_img.getGraphics().drawImage(image,0,0,275,200,null);
    		img_path = path; //brukes i forbindelse med pdf
    		img = new ImageIcon(new_img);
    		
    	}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

	}
	public ImageIcon get_normalSize(){

		if(normal_size!=null){

			return normal_size;
   						
	}
		return new ImageIcon(path);
	}
	public ImageIcon get_img(){
		if(img!=null){
			
			return img;
		}	
		return new ImageIcon(path);
	}
	public String get_path(){
	
		return img_path;
	}
}//end of Produkt class
class MTable extends AbstractTableModel{
	String[] columnNames = {"Varenummer","Beskrivelse","kategori","Bilde"};
  	Object[][] data;
  	private int lengde;
  	private int count=0;
  	
  	MTable(ArrayList<Produkt> produktlista){
		lengde = produktlista.size();
		data = new Object[lengde][4];

		visLista(produktlista); //denne skaper new Imageicon() på plass data[count][3] = prod.get_img();

	}//end of constructor
	public void visLista(ArrayList<Produkt> produktlista){
		count=0;
		for(Produkt prod : produktlista){
			data[count][0] = prod.get_produktNr();
			data[count][1] = prod.get_beskrivelse();
			data[count][2] = prod.get_kategori();
			data[count][3] = prod.get_img();
			count++;
		}
	}
	public void refresh(int pos,String prodNr,String besk,String kateg, ImageIcon img){
		data[pos][0] = prodNr;
		data[pos][1] = besk;
		data[pos][2] = kateg;
		data[pos][3] = img;
	}
	public void setSize(int size){
		lengde = size;
		data = new Object[lengde][4];
	}

	public void set_image(Produkt p,int selection){ //Får en produkt, og gir den et bilde

		data[selection][3] = p.get_img();
	}
	public String getColumnName(int kolonne){
		return columnNames[kolonne];
	}
	public String get_vare(int row){
		String varenr = (String) data[row][0];
		return varenr;
	}
	
	public Class getColumnClass(int kolonne){
		return data[0][kolonne].getClass();
	}
	
	public Object getValueAt(int rad, int kolonne){
		return data[ rad][ kolonne];
	}
	public int getColumnCount(){
		return data[0].length;
	}
	public int getRowCount(){
		 return data.length;
	}
}

class Vindu extends JFrame {

  	private int lengde;
  	private int count=0;
  	private JButton add_img;
  	private JButton sok;
  	private JButton printLista;
  	private JButton createPdf;
  	private JButton visBilde;
  	private JButton sokVareNr;
  	private JButton sokBeskrivelse;
  	private KommandoLytter lytter;
  	private MTable tabl;
  	private JScrollPane pane;
  	private JPanel westCenter;
  	private final JTextFieldHint sokefelt;
  	private final JTextFieldHint sokefelt2;
  	private boolean restored_session = false;
 
  	RegListe regListe;
  	JTable table;

	Vindu(){
		super("Goodtech produkt katalog");
		lesFil(); //instansierer regListe
		this.setContentPane(new JLabel(new ImageIcon("background2.png")));
		setLayout(new FlowLayout());
		
		JPanel mainpanel = new JPanel();
		mainpanel.setPreferredSize(new Dimension(1050,775));
		mainpanel.setOpaque(false);
		mainpanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		this.getContentPane().add(mainpanel);
		tabl = new MTable(regListe.getList());
		//table = new JTable(tabl);
		//table.setShowGrid(true);
		
		//table.setGridColor(Color.BLACK);
		//this.getContentPane().setLayout(new BorderLayout(5,5));
		//table.setRowHeight(200);
		//table.getColumnModel().getColumn(1).setPreferredWidth(200); //produkt beskrivelse
		//table.getColumnModel().getColumn(3).setPreferredWidth(200); //bildet

		pane = new JScrollPane(table);
		//createAlternating(tabl);
		sokefelt = new JTextFieldHint();
		sokefelt2 = new JTextFieldHint();
		sokefelt.setHint("Søk etter varenummer");
		sokefelt2.setHint("Søk etter beskrivelse");
		sokVareNr = new JButton("Søk");
		sokBeskrivelse = new JButton("Søk");
		add_img = new JButton();
		visBilde = new JButton();
		printLista = new JButton();

		ImageIcon icon = new ImageIcon(getClass().getResource("pdf.gif"));
		ImageIcon icon2 = new ImageIcon(getClass().getResource("image_add.png"));
		ImageIcon icon3 = new ImageIcon(getClass().getResource("back.png"));
		ImageIcon icon4 = new ImageIcon(getClass().getResource("magnifying_glass.png"));

		createPdf = new JButton();
		createPdf.setIcon(icon);
		add_img.setIcon(icon2);
		printLista.setIcon(icon3);
		visBilde.setIcon(icon4);

		gbc.gridx=0; gbc.gridy=0; mainpanel.add(new JLabel("Søk etter varenummer  : "),gbc);
		gbc.gridx=1; gbc.ipadx=700; mainpanel.add(sokefelt,gbc); gbc.ipadx=1;
		gbc.gridx=2; gbc.ipady=10;  mainpanel.add(sokVareNr,gbc); gbc.ipady=1;
		gbc.gridx=0; gbc.gridy=1; mainpanel.add(new JLabel("Søk etter beskrivelse : "),gbc);
		gbc.gridx=1; mainpanel.add(sokefelt2,gbc); gbc.gridx=2; gbc.ipady=10; mainpanel.add(sokBeskrivelse,gbc);
		gbc.ipady=1; gbc.weighty=5;

		gbc.gridwidth=4; gbc.ipady=630; gbc.ipadx=1000; gbc.gridx=0; gbc.gridy=3; mainpanel.add(createAlternating(tabl),gbc);
		gbc.gridwidth=1;

		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		gbc.fill = GridBagConstraints.NONE; //gbc.ipady=30; gbc.ipadx=1; gbc.gridx=0; gbc.gridy=4; mainpanel.add(add_img,gbc);
		//gbc.gridx=1; mainpanel.add(visBilde,gbc); gbc.gridx=2; mainpanel.add(printLista,gbc); 
		
		
		JPanel buttonpanel = new JPanel(new GridLayout(2,4,5,0));
		buttonpanel.setOpaque(false);
		buttonpanel.add(add_img); buttonpanel.add(visBilde); buttonpanel.add(printLista); buttonpanel.add(createPdf);
		buttonpanel.add(new JLabel("  Legg til bilde"));buttonpanel.add(new JLabel(" Vis større bilde"));
		buttonpanel.add(new JLabel(" Tilbake til lista")); buttonpanel.add(new JLabel("     Lag PDF"));
		gbc.gridwidth=2; gbc.ipady=40; gbc.ipadx=10; gbc.gridx=0; gbc.gridy=4;  mainpanel.add(buttonpanel,gbc);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item1 = new JMenuItem("Legg til ny vare");
		JMenuItem item2 = new JMenuItem("Lagre");
		JMenuItem item3 = new JMenuItem("Avslutte");

		menu.add(item1);
		menu.add(item2);
		menu.add(item3);
		menuBar.add(menu);


		lytter = new KommandoLytter();
		//Lytter til knappene
		add_img.addActionListener(lytter);
		sokVareNr.addActionListener(lytter);
		sokBeskrivelse.addActionListener(lytter);
		printLista.addActionListener(lytter);
		createPdf.addActionListener(lytter);
		visBilde.addActionListener(lytter);
	
		this.setJMenuBar(menuBar);
		pack();
		//getRootPane().setDefaultButton(sokBeskrivelse);
		sokBeskrivelse.setAction(action1);
		sokVareNr.setAction(action2);

		//getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
		sokefelt2.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "COPY");
		sokefelt2.getActionMap().put("COPY", action1);

		sokefelt.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "COPY");
		sokefelt.getActionMap().put("COPY",action2);

		this.setSize(1050,850);
		this.setMaximumSize(new Dimension(1050, 800));
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setVisible(true);
		if(restored_session){
			JOptionPane.showMessageDialog(this, "Restored from last session","Gjennopprettet", JOptionPane.PLAIN_MESSAGE);
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		table.requestFocus();

	}

    private Action action1 = new AbstractAction("Søk")
    {
        public void actionPerformed(ActionEvent e)
        {
   
   			sokEtterBeskrivelse();
      
        }
    };
    
    private Action action2 = new AbstractAction("Søk")
    {
        public void actionPerformed(ActionEvent e)
        {
   
   			sokEtterVareNr();
      
        }
    };

	public String file_chooser(){
		JFileChooser filvelger = new JFileChooser();
		filvelger.getCurrentDirectory();
		
		int resultat = filvelger.showOpenDialog(this);

		if(resultat == JFileChooser.APPROVE_OPTION){
			File fil = filvelger.getSelectedFile();
			return fil.getPath();
		}else{
			return null;
		}
	}
	public JComponent createAlternating(AbstractTableModel model)
	{
		table = new JTable( model )
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);

				//  Alternate row color

				if (!isRowSelected(row))                             //Color.LIGHT_GRAY
					c.setBackground(row % 2 == 0 ? getBackground() : Color.WHITE);

				return c;
			}
		};

		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setShowGrid(true);
		table.setGridColor(Color.BLACK);
		table.setRowHeight(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(200); //produkt beskrivelse
		table.getColumnModel().getColumn(3).setPreferredWidth(200); //bildet
		table.changeSelection(0, 0, false, false);
		return new JScrollPane( table );
	}

	public void legge_til_automatisk(){
		//This one should be choosed by the user
		File []files = new File("/Users/Nicolas/Desktop/Project_katalog/Bilder/katalog").listFiles();
		int teller = 0;

		for(File file : files){
			
			String varenr = file.getName().replaceAll(".JPG","");
		
			if(regListe.sok(varenr)!=null){

				Produkt p = regListe.sok(varenr);
				//System.out.println(p.get_beskrivelse());
				p.set_image(file.getPath());
			}else{
				teller++;
				System.out.println("Finnes ikke i min liste : "+varenr);
			}
			//System.out.println("Varer uten bilder : " +teller);
			//System.out.println(varenr);

			//System.out.println(file.getPath());
		}
		//regListe.getList();
		//tabl.fireTableDataChanged();
	}

/*
 * Metoden som leser fra fil. Blir kalt hver gang programmet startes
 */
	public void lesFil(){
		try(ObjectInputStream innfil = new ObjectInputStream(new FileInputStream("SAVED_STATE.data")))
		{
			regListe = (RegListe) innfil.readObject();
			restored_session = true;
			System.out.println("leste fra SAVED_STATE");
		}
		catch(ClassNotFoundException cnfe)
		{
			regListe = new RegListe();
			System.out.println("fant ikke SAVED_STATE 661");
			regListe.read_file("lista.data");
			legge_til_automatisk();
		}
		catch(FileNotFoundException fne)
		{
			regListe = new RegListe();
			System.out.println("fant ikke SAVED_STATE 668");
			regListe.read_file("lista.data");
			legge_til_automatisk();
		}
		catch(IOException ioe)
		{
			regListe = new RegListe();
			System.out.println("fant ikke SAVED_STATE 675");
			regListe.read_file("/Users/Nicolas/Desktop/Project_katalog/lista.data");
			legge_til_automatisk();
		}
	}

	public void skrivTilFil(){

		try (ObjectOutputStream utfil = new ObjectOutputStream(new FileOutputStream("SAVED_STATE.data")))
		{
			if(regListe!=null){ //The program will not write if no objects are initilized
				utfil.writeObject(regListe);
				regListe = null;
			}
		}
		catch( NotSerializableException nse )
		{
			JOptionPane.showMessageDialog(this, "Objektet er ikke serialisert!","Problem", JOptionPane.ERROR_MESSAGE);
		}
		catch( IOException ioe )
		{
			JOptionPane.showMessageDialog(this, "Problem med utskrift til fil.","Problem", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void sokEtterVareNr(){
		String test = sokefelt.getText();
	
		if(test.length()>0&&!test.equals("Søk etter varenummer")){
			
			Produkt p = regListe.sok(test);

			if(p!=null){
				for(int i=0; i<tabl.data.length;i++){
					tabl.refresh(i,"","","",null);	
				}                                                                   
			tabl.refresh(0,p.get_produktNr(),p.get_beskrivelse(),p.get_kategori(),p.get_img()); 
			tabl.fireTableDataChanged();
			sokefelt.setHint("");
			//setter fokus på varen vi fant
			JViewport viewport = (JViewport)table.getParent();
        	Rectangle rect = table.getCellRect(0, 0, true);
        	Point pt = viewport.getViewPosition();
        	rect.setLocation(rect.x-pt.x, rect.y-pt.y);
        	table.scrollRectToVisible(rect);
			}else{
				JOptionPane.showMessageDialog(Vindu.this,"Ingen resultater samsvarte med søket ditt","Prøv igjen",JOptionPane.PLAIN_MESSAGE);
			}
		sokefelt.setHint("Søk etter varenummer");
		}else{
			JOptionPane.showMessageDialog(Vindu.this,"Du må skrive noe i sokefeltet","Prøv igjen",JOptionPane.PLAIN_MESSAGE);
			sokefelt.setHint("Søk etter varenummer");
		}

	}
	public void sokEtterBeskrivelse(){
		String mintxt = sokefelt2.getText();
		mintxt=mintxt.toLowerCase();
		boolean funnet = false;
		int teller=0;
		int lengde = regListe.traverser(mintxt).size();

		if(lengde>0){
			tabl.setSize(lengde);
			tabl.visLista(regListe.traverser(mintxt));
			tabl.fireTableDataChanged();
			JOptionPane.showMessageDialog(Vindu.this,"Fant "+lengde+" resultat(er) som samsvarer med søket ditt","Resultat",JOptionPane.PLAIN_MESSAGE);
			sokefelt2.setHint("Søk etter beskrivelse");
		}else{
			JOptionPane.showMessageDialog(Vindu.this,"Ingen resultater samsvarte med søket ditt","Prøv igjen",JOptionPane.PLAIN_MESSAGE);
			sokefelt2.setHint("Søk etter beskrivelse");
		}
		/*
		if(!mintxt.equals("søk etter beskrivelse")&&mintxt.length()>0){		
		for(Produkt p : regListe.traverser(mintxt)){
			System.out.println(p.get_beskrivelse());
			funnet = true;
			tabl.refresh(teller,p.get_produktNr(),p.get_beskrivelse(),p.get_kategori(),p.get_img());
			tabl.fireTableDataChanged();
			teller++; 
		}
	
		if(funnet){
			for(int i=teller; i<tabl.data.length;i++){
				tabl.refresh(i,"","","",null);	
			}
			JViewport viewport = (JViewport)table.getParent();
        	Rectangle rect = table.getCellRect(0, 0, true);
        	Point pt = viewport.getViewPosition();
        	rect.setLocation(rect.x-pt.x, rect.y-pt.y);
        	table.scrollRectToVisible(rect);
			String antall = Integer.toString(teller);
			JOptionPane.showMessageDialog(Vindu.this,"Fant "+antall+" resultat(er) som samsvarer med søket ditt","Resultat",JOptionPane.PLAIN_MESSAGE); 
		}else{
			JOptionPane.showMessageDialog(Vindu.this,"Ingen resultater samsvarte med søket ditt","Prøv igjen",JOptionPane.PLAIN_MESSAGE);
		}
		sokefelt2.setHint("Søk etter beskrivelse");
		}
		*/

	}
	private class KommandoLytter implements ActionListener{ //lytter til knapper

		public void actionPerformed(ActionEvent e){
			
			if(e.getSource()==add_img){
				int selection = table.getSelectedRow();
				if (selection>-1) {
					String path = file_chooser();
					if(path!=null){
						Produkt p = regListe.sok(tabl.get_vare(selection));
						if(p!=null){
							p.set_image(path);
							tabl.set_image(p,selection);
							tabl.fireTableDataChanged();
						}	
					}
				}else{
					JOptionPane.showMessageDialog(Vindu.this,"Du må velge en produkt først","Velg produkt",JOptionPane.PLAIN_MESSAGE);
				}
			}else if(e.getSource()==sokVareNr){

				//sokEtterVareNr();

			}else if(e.getSource()==sokBeskrivelse){
		
				//sokEtterBeskrivelse();

			}else if(e.getSource()==printLista){
				tabl.setSize(regListe.getList().size()); //setter table til sin opprinnelig størrelse
				tabl.visLista(regListe.getList()); //Setter alt data tilbake
				tabl.fireTableDataChanged(); //repaints the table
				sokefelt.setHint("Søk etter varenummer");
				sokefelt2.setHint("Søk etter beskrivelse");

			}else if(e.getSource()==createPdf){
				
				JFrame frame = new JFrame();
				frame.setLayout(new FlowLayout());
				JPanel prg = new JPanel();

				JProgressBar progressBar=new JProgressBar();
				progressBar.setPreferredSize(new Dimension(180, 40));
				progressBar.setIndeterminate(true);
	
				new Thread(new Runnable() {
					@Override
         			public void run() {
         				regListe.create_pdf(); //oppretter PDF filen
         				SwingUtilities.invokeLater(new Runnable() {
         				@Override
                 		public void run() {

                     		progressBar.setIndeterminate(false);
                     		JOptionPane.showMessageDialog(Vindu.this,"Katalogen er nå opprettet i PDF-format","PDF opprettet",JOptionPane.PLAIN_MESSAGE);
                     		frame.dispose();

                 		} 
         				
         				});	
         			}		
				}).start();

				prg.add(progressBar);
				prg.add(new JLabel("Vennligst vent mens PDF filen blir opprettet"));
				frame.getContentPane().add(prg);
				
				frame.setSize(500,100);
				frame.setLocationRelativeTo(null); // under testing
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   			
			} else if(e.getSource()==visBilde){
				int selection = table.getSelectedRow();
				if (selection>-1) {
					
					Produkt p = regListe.sok(tabl.get_vare(selection));

					if(p!=null){
						//String bigger_img = p.get_path();
						ImageIcon img_toShow = p.get_normalSize();
						ShowBigger bilde = new ShowBigger(img_toShow);
					}	
					
				}else{
					JOptionPane.showMessageDialog(Vindu.this,"Du må velge en produkt først","Velg produkt",JOptionPane.PLAIN_MESSAGE);
				}
			}

		}
	}
}//end of Vindu class
class ImageRenderer extends DefaultTableCellRenderer {
  JLabel lbl = new JLabel();
  ImageIcon icon = new ImageIcon(getClass().getResource("lindeberg.png"));

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
  	lbl.setText((String) value);
 	lbl.setIcon(icon);
    return lbl;
  }
}
class ShowBigger extends JFrame{
	ImageIcon img;

	ShowBigger(ImageIcon img){
		//img = new ImageIcon(path);
		this.img = img;
		this.getContentPane().setLayout(new BorderLayout());
		
		JLabel bilde = new JLabel(img);
		JScrollPane pane = new JScrollPane(bilde);
		
		this.getContentPane().add(pane,"Center");
		//this.getContentPane().add(new JLabel("Bilde plassering     : "+path),"South");
		this.setSize(800,650);
		this.setVisible(true);	
	}
 
}
class NonEditableModel extends DefaultTableModel { //makes JTables non-editable

    NonEditableModel(Object[][] data, String[] columnNames) {
        super(data, columnNames);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}//end of NonEditableModel class

class JTextFieldHint extends JTextField implements FocusListener {
 
    private final Font fontLost = new Font("Monaco", Font.ITALIC, 18);
    private final Font fontGained = new Font("Monaco", Font.PLAIN, 18);
    private final Color colorLost = Color.LIGHT_GRAY;
    private final Color colorGained = Color.BLACK;
    private String hint;
 
    @SuppressWarnings("LeakingThisInConstructor")
    public JTextFieldHint() {
        addFocusListener(this);
    }
 
    public void setHint(String hint) {
        setForeground(colorLost);
        setFont(fontLost);
        setText(hint);
        this.hint = hint;
    }
 
    public String getHint() {
        return hint;
    }
 
    @Override
    public void focusGained(FocusEvent e) {
        if (getText().equals(getHint())) {
            setText("");
            setFont(fontGained);
            setForeground(colorGained);
        } else {
            setForeground(colorGained);
            setFont(fontGained);
            setText(getText());
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().length() <= 0) {
            setHint(getHint());
        } else {
            setForeground(colorGained);
            setFont(fontGained);
            setText(getText());
        }
    }
}





