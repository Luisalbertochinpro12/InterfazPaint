
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Paint extends javax.swing.JFrame {

    //DECLARACION DE VARIABLES
    private File archivo = null; //Referencia al archivo
    private BufferedImage image;
    private Point lastPoint;
    private int brushWidth = 1;// ancho del pincel
    private Color currentColor = Color.BLACK; // color inicial del pincel y es la variable encargada de dar el color al pincel
    //variables de texto
    private Color textColor = Color.BLACK;// color del texto al iniciar
    private String fuentes = "Arial";// tipo de fuente de letra
    private int tamanoText = 30;// tamaño de letra
    private int estiloText = Font.PLAIN;// estilo de letra
    //variables de borrador
    private Color borrador = Color.WHITE;
    private String seleccion = "";// la seleccion de que herramienta escogio
    private int alto = 0;
    private int ancho = 0;
    private String textoPaint = "";
    private int x, y; 
    private String tipoFigura = "linea";
    private boolean guardaste=true;// boleano que verfica si guardaste
    private int confirmar=0;

    //FIN DE DECLARACION
    public Paint() {
        initComponents();
        trianguloBtn.setEnabled(false);
        lineaBtn.setEnabled(false);
        circuloBtn.setEnabled(false);
        cuadradoBtn.setEnabled(false);
        setLocationRelativeTo(null);
        fuenteBx.setVisible(false);
        tamanoBx.setVisible(false);
        NegritasBt.setVisible(false);
        CursivaBt.setVisible(false);
        
        image = new BufferedImage(LienzoPanel.getWidth(), LienzoPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        //Funcionamiento de la herramienta TEXTO
        LienzoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // consigue la posicion del mouse
                x = e.getX();
                y = e.getY();
                if ("texto".equals(seleccion)) {
                    do {
                        textoPaint = JOptionPane.showInputDialog(LienzoPanel, "Ingrese un texto", "TEXTO", JOptionPane.QUESTION_MESSAGE);// crea un mensaje para introduccion el texto
                        guardaste=false;
                        if (textoPaint == null) {// si oprime el boton de cancelar
                            return;//sale del dialogo
                            
                        }
                    } while (textoPaint.trim().isEmpty());// no dejara que pueda guardar si no introdujo un texto
                    paintComponent(LienzoPanel.getGraphics());
                }
            }
        });

        //funcionamiento de la herramienta PINCEL
        //detecta el clic del mouse en el panel y guardando la última posición del mouse en el panel 
        //para poder usarla en la función de dibujo posteriormente.
        //solo sirve en el panel esto debido a pincelPanel
        LienzoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });
        // detecta cuando se mueve el mouse mientras esta presionado
        //solo sirve en el panel esto debido a la primera sentenica pincelPanel
        LienzoPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            // Cuando se arrastra el mouse, el método mouseDragged() se llama y obtiene las coordenadas x e y
            public void mouseDragged(MouseEvent e) {
                if ("pincel".equals(seleccion)) {
                    Graphics g = LienzoPanel.getGraphics();//se utiliza para dibujar en el panel
                    Graphics2D g2d = (Graphics2D) g;// Este objeto Graphics se convierte en un objeto Graphics2D, que permite dibujar líneas más complejas que el objeto Graphics regular.
                    g2d.setColor(currentColor);// se establece el color
                    g2d.setStroke(new BasicStroke(brushWidth));// se establece el ancho
                    g2d.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());//Luego, se dibuja una línea desde la última posición del mouse (lastPoint) hasta la posición actual del mouse (e.getX() y e.getY()).
                    lastPoint = e.getPoint();//se actualiza lastPoint para que apunte a la posición actual del mouse
                    guardaste=false;

                }
            }
        });
//figuras
        if ("figuras".equals(seleccion)) {}// SI BORRAS ESTE IF PUEDES USAR FIGURAS PERO NO PINCEL Y TEXTO, SI SE MANTIENE PUEDES USAR PINCEL Y TEXTO PERO NO FIGURAS
            LienzoPanel.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    lastPoint = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                    Graphics g = LienzoPanel.getGraphics();
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(currentColor);
                    BasicStroke grosor = new BasicStroke(1);
                    g2d.setStroke(grosor);

                    int x1 = lastPoint.x;
                    int y1 = lastPoint.y;
                    int x2 = e.getX();
                    int y2 = e.getY();
                    if ("figuras".equals(seleccion)) {
                        switch (tipoFigura) {
                            case "linea":
                                // Calcular la distancia y el ángulo entre los dos puntos
                                double dx = x2 - x1;
                                double dy = y2 - y1;
                                double distancia = Math.sqrt(dx * dx + dy * dy);
                                double angulo = Math.atan2(dy, dx);

                                // Calcular los valores X e Y necesarios para dibujar la línea recta
                                int x3 = (int) (x1 + distancia * Math.cos(angulo));
                                int y3 = (int) (y1 + distancia * Math.sin(angulo));
                                g2d.setStroke(new BasicStroke(brushWidth));// se establece el ancho
                                g2d.drawLine(x1, y1, x3, y3);
                                guardaste=false;

                                break;
                            case "cuadrado":
                                int rectX = Math.min(x1, x2);
                                int rectY = Math.min(y1, y2);
                                int rectWidth = Math.abs(x1 - x2);
                                int rectHeight = Math.abs(y1 - y2);

                                g2d.fillRect(rectX, rectY, rectWidth, rectHeight);
                                guardaste=false;

                                break;
                            case "circulo":
                                int rectXc = Math.min(x1, x2);
                                int rectYc = Math.min(y1, y2);
                                int rectWidthc = Math.abs(x1 - x2);
                                int rectHeightc = Math.abs(y1 - y2);

                                g2d.fillOval(rectXc, rectYc, rectWidthc, rectHeightc);
                                guardaste=false;

                                break;
                            case "triangulo":
                                int[] xPoints = {x1, x2, (x1 + x2) / 2};
                                int[] yPoints = {y2, y2, y1};
                                g2d.fillPolygon(xPoints, yPoints, 3);
                                guardaste=false;

                                break;
                        }
                    }
                }
            });
        // LLAVE DEL IF
        
        //si le da a la x para cerrar 
                         this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {  
            @Override
            public void windowClosing(WindowEvent e) {
                int closeOperation = Paint.this.getDefaultCloseOperation();
                 if (closeOperation == JFrame.DO_NOTHING_ON_CLOSE) {
                 if(guardaste==false)
                {
        confirmar=JOptionPane.showInternalConfirmDialog(null,"Se detecto una actualizacion en su documento, ¿desea guardarlo?", "INFORMACIÓN",JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);  
             if (confirmar==0) // si es 0 significado que le dio a "si"
             {
              if (archivo != null) {
              Timer timer = new Timer(600, e1 -> {
              guardarImagen(LienzoPanel, archivo, "Guardar");
               }
               );
               timer.setRepeats(false);
               timer.start();
                } else 
              {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
            chooser.setFileFilter(filtro);
            int seleccionFile = chooser.showSaveDialog(chooser);
            if (seleccionFile == JFileChooser.APPROVE_OPTION) {
                archivo = chooser.getSelectedFile();
                if (!archivo.getName().endsWith(".png") && !archivo.getName().endsWith(".jpg") && !archivo.getName().endsWith(".gif")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                    //Si no se ingresa una extensión, el archivo se guarda como jpg por defecto
                    }

                //El timer está puesto para que no se guarde la imagen con el FileChooser Abierto y este no salga
                Timer timer = new Timer(1000, e1 -> {
                    guardarImagen(LienzoPanel, archivo, "GuardarComo");
                    }
                    );
                    timer.setRepeats(false);
                    timer.start();
                    }
              }
             guardaste=true;   
             }else// si puso que "no"
             {
                Menu menu = new Menu();
                menu.setVisible(true);
                menu = null;
                dispose();  
             }    
             
        } 
        Menu menu = new Menu();
        menu.setVisible(true);
        menu = null;
        dispose();
        
                 }}});
    }

    //el manejo de convertir el TEXTO en DIBUJO
    private void paintComponent(Graphics g) {
        // dependiendo si el boton este activo o no, cambiara el estilo de letra
        if (NegritasBt.isSelected() && !CursivaBt.isSelected()) {
            Font fuente = new Font(fuentes, estiloText, tamanoText);
            g.setFont(fuente);// para que se aplique el cambio
        } else if (CursivaBt.isSelected() && !NegritasBt.isSelected()) {
            Font fuente = new Font(fuentes, estiloText, tamanoText);
            g.setFont(fuente);
        } else if (CursivaBt.isSelected() && NegritasBt.isSelected()) {
            Font fuente = new Font(fuentes, Font.BOLD + Font.ITALIC, tamanoText);
            g.setFont(fuente);
        } else {
            Font fuente = new Font(fuentes, Font.PLAIN, tamanoText);
            g.setFont(fuente);
        }
        g.setColor(textColor);
        g.drawString(textoPaint, x, y);//se dibuja en la ubicacion indicada

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HerramientaPanel = new javax.swing.JPanel();
        PincelBtn = new javax.swing.JButton();
        TextoBtn = new javax.swing.JButton();
        FormasBtn = new javax.swing.JButton();
        EscalaBtn = new javax.swing.JButton();
        BorradorBtn = new javax.swing.JButton();
        menu = new javax.swing.JLabel();
        LienzoPanel = new javax.swing.JScrollPane();
        LienzoPanel1 = new javax.swing.JPanel();
        HerramientaExtraPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cajaGrosor = new javax.swing.JComboBox<>();
        ColorLbl = new javax.swing.JLabel();
        sliderBorrador = new javax.swing.JSlider();
        fuenteBx = new javax.swing.JComboBox<>();
        tamanoBx = new javax.swing.JComboBox<>();
        NegritasBt = new javax.swing.JToggleButton();
        CursivaBt = new javax.swing.JToggleButton();
        lineaBtn = new javax.swing.JButton();
        trianguloBtn = new javax.swing.JButton();
        cuadradoBtn = new javax.swing.JButton();
        circuloBtn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        nuevo = new javax.swing.JMenuItem();
        abrir = new javax.swing.JMenuItem();
        guardar = new javax.swing.JMenuItem();
        guardarComo = new javax.swing.JMenuItem();
        cerrar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        HerramientaPanel.setBackground(new java.awt.Color(204, 204, 204));

        PincelBtn.setBackground(new java.awt.Color(204, 204, 204));
        PincelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/picel.png"))); // NOI18N
        PincelBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PincelBtn.setContentAreaFilled(false);
        PincelBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PincelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PincelBtnActionPerformed(evt);
            }
        });

        TextoBtn.setBackground(new java.awt.Color(204, 204, 204));
        TextoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/texto.png"))); // NOI18N
        TextoBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TextoBtn.setContentAreaFilled(false);
        TextoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        TextoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextoBtnActionPerformed(evt);
            }
        });

        FormasBtn.setBackground(new java.awt.Color(204, 204, 204));
        FormasBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/formas.png"))); // NOI18N
        FormasBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        FormasBtn.setContentAreaFilled(false);
        FormasBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        FormasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FormasBtnActionPerformed(evt);
            }
        });

        EscalaBtn.setBackground(new java.awt.Color(204, 204, 204));
        EscalaBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/escala.png"))); // NOI18N
        EscalaBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        EscalaBtn.setContentAreaFilled(false);
        EscalaBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EscalaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EscalaBtnActionPerformed(evt);
            }
        });

        BorradorBtn.setBackground(new java.awt.Color(204, 204, 204));
        BorradorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/goma-de-borrar.png"))); // NOI18N
        BorradorBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        BorradorBtn.setContentAreaFilled(false);
        BorradorBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BorradorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorradorBtnActionPerformed(evt);
            }
        });

        menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/textoImagenes/home dark icon.png"))); // NOI18N
        menu.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        menu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout HerramientaPanelLayout = new javax.swing.GroupLayout(HerramientaPanel);
        HerramientaPanel.setLayout(HerramientaPanelLayout);
        HerramientaPanelLayout.setHorizontalGroup(
            HerramientaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HerramientaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BorradorBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(HerramientaPanelLayout.createSequentialGroup()
                .addGroup(HerramientaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PincelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextoBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EscalaBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FormasBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(HerramientaPanelLayout.createSequentialGroup()
                        .addComponent(menu)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        HerramientaPanelLayout.setVerticalGroup(
            HerramientaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HerramientaPanelLayout.createSequentialGroup()
                .addComponent(PincelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TextoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(EscalaBtn)
                .addGap(18, 18, 18)
                .addComponent(FormasBtn)
                .addGap(18, 18, 18)
                .addComponent(BorradorBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(143, 143, 143)
                .addComponent(menu)
                .addGap(42, 42, 42))
        );

        getContentPane().add(HerramientaPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 510));

        LienzoPanel.setBackground(new java.awt.Color(255, 255, 255));
        LienzoPanel.setBorder(null);
        LienzoPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        LienzoPanel.setToolTipText("");
        LienzoPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        LienzoPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout LienzoPanel1Layout = new javax.swing.GroupLayout(LienzoPanel1);
        LienzoPanel1.setLayout(LienzoPanel1Layout);
        LienzoPanel1Layout.setHorizontalGroup(
            LienzoPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        LienzoPanel1Layout.setVerticalGroup(
            LienzoPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 506, Short.MAX_VALUE)
        );

        LienzoPanel.setViewportView(LienzoPanel1);

        getContentPane().add(LienzoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 0, 640, 450));

        HerramientaExtraPanel.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setText("Grosor");

        cajaGrosor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "chico", "mediano", "grande", "extra grande" }));
        cajaGrosor.setBorder(null);
        cajaGrosor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cajaGrosor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cajaGrosorActionPerformed(evt);
            }
        });

        ColorLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/color.png"))); // NOI18N
        ColorLbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ColorLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ColorLblMouseClicked(evt);
            }
        });

        sliderBorrador.setBackground(new java.awt.Color(153, 153, 153));
        sliderBorrador.setMaximum(80);
        sliderBorrador.setMinimum(2);
        sliderBorrador.setValue(2);
        sliderBorrador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sliderBorrador.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBorradorStateChanged(evt);
            }
        });

        fuenteBx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Arial", "Calibri", "Cambria", "Comic Sans MS", "Consolas", "Courier New", "Georgia", "Impact", "Times New Roman", "Trebuchet MS", "Verdana" }));
        fuenteBx.setBorder(null);
        fuenteBx.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        fuenteBx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fuenteBxActionPerformed(evt);
            }
        });

        tamanoBx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "12", "14", "16", "18", "20", "24", "26", "30", "32", "40", "50" }));
        tamanoBx.setBorder(null);
        tamanoBx.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tamanoBx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tamanoBxActionPerformed(evt);
            }
        });

        NegritasBt.setBackground(new java.awt.Color(153, 153, 153));
        NegritasBt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/negritaIcon.png"))); // NOI18N
        NegritasBt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        NegritasBt.setContentAreaFilled(false);
        NegritasBt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        NegritasBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NegritasBtActionPerformed(evt);
            }
        });

        CursivaBt.setBackground(new java.awt.Color(153, 153, 153));
        CursivaBt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/italicoIcon.png"))); // NOI18N
        CursivaBt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        CursivaBt.setContentAreaFilled(false);
        CursivaBt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CursivaBt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CursivaBtActionPerformed(evt);
            }
        });

        lineaBtn.setBackground(new java.awt.Color(153, 153, 153));
        lineaBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/linea-diagonalIcon.png"))); // NOI18N
        lineaBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lineaBtn.setContentAreaFilled(false);
        lineaBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lineaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineaBtnActionPerformed(evt);
            }
        });

        trianguloBtn.setBackground(new java.awt.Color(153, 153, 153));
        trianguloBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/trianguloIcon.png"))); // NOI18N
        trianguloBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        trianguloBtn.setContentAreaFilled(false);
        trianguloBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        trianguloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trianguloBtnActionPerformed(evt);
            }
        });

        cuadradoBtn.setBackground(new java.awt.Color(153, 153, 153));
        cuadradoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cuadradoIcon.png"))); // NOI18N
        cuadradoBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cuadradoBtn.setContentAreaFilled(false);
        cuadradoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cuadradoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cuadradoBtnActionPerformed(evt);
            }
        });

        circuloBtn.setBackground(new java.awt.Color(153, 153, 153));
        circuloBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/circuloIcon.png"))); // NOI18N
        circuloBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        circuloBtn.setContentAreaFilled(false);
        circuloBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        circuloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                circuloBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout HerramientaExtraPanelLayout = new javax.swing.GroupLayout(HerramientaExtraPanel);
        HerramientaExtraPanel.setLayout(HerramientaExtraPanelLayout);
        HerramientaExtraPanelLayout.setHorizontalGroup(
            HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HerramientaExtraPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(HerramientaExtraPanelLayout.createSequentialGroup()
                        .addComponent(cajaGrosor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fuenteBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(HerramientaExtraPanelLayout.createSequentialGroup()
                        .addComponent(NegritasBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CursivaBt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tamanoBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ColorLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineaBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trianguloBtn)
                .addGap(12, 12, 12)
                .addComponent(cuadradoBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(circuloBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(sliderBorrador, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        HerramientaExtraPanelLayout.setVerticalGroup(
            HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HerramientaExtraPanelLayout.createSequentialGroup()
                .addGroup(HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, HerramientaExtraPanelLayout.createSequentialGroup()
                        .addGroup(HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fuenteBx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(cajaGrosor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tamanoBx, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NegritasBt, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(CursivaBt, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, HerramientaExtraPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(HerramientaExtraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sliderBorrador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ColorLbl)
                            .addComponent(lineaBtn)
                            .addComponent(trianguloBtn)
                            .addComponent(cuadradoBtn)
                            .addComponent(circuloBtn))))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        getContentPane().add(HerramientaExtraPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, 680, 60));

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenu1.setText("Archivo");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        nuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        nuevo.setText("Nuevo");
        nuevo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        jMenu1.add(nuevo);

        abrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        abrir.setText("Abrir");
        abrir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirActionPerformed(evt);
            }
        });
        jMenu1.add(abrir);

        guardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        guardar.setText("Guardar");
        guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        jMenu1.add(guardar);

        guardarComo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        guardarComo.setText("Guardar Como");
        guardarComo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        guardarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarComoActionPerformed(evt);
            }
        });
        jMenu1.add(guardarComo);

        cerrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        cerrar.setText("Cerrar");
        cerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarActionPerformed(evt);
            }
        });
        jMenu1.add(cerrar);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BorradorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorradorBtnActionPerformed
        LienzoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        seleccion = "borrador";
        trianguloBtn.setEnabled(false);
        lineaBtn.setEnabled(false);
        circuloBtn.setEnabled(false);
        cuadradoBtn.setEnabled(false);
        //sliderBorrador.setVisible(true);

        //detecta el clic del mouse en el panel y guardando la última posición del mouse en el panel
        //para poder usarla en la función de dibujo posteriormente.
        //solo sirve en el panel esto debido a pincelPanel
        LienzoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });
        // detecta cuando se mueve el mouse mientras esta presionado
        //solo sirve en el panel esto debido a la primera sentenica pincelPanel
        LienzoPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            // Cuando se arrastra el mouse, el método mouseDragged() se llama y obtiene las coordenadas x e y
            public void mouseDragged(MouseEvent e) {
                if ("borrador".equals(seleccion)) {
                    Graphics circulo = LienzoPanel.getGraphics();//se utiliza para dibujar en el panel
                    Graphics2D circulo2d = (Graphics2D) circulo;
                    circulo2d.setColor(borrador);

                    //si el borrador es menor a 10
                    if (sliderBorrador.getValue() < 10) {
                        circulo2d.fillOval(e.getX(), e.getY(), sliderBorrador.getValue(), sliderBorrador.getValue());

                    }
                    // de 10 a 24
                    if (sliderBorrador.getValue() >= 10 && sliderBorrador.getValue() < 25) {
                        circulo2d.fillOval(e.getX() - 5, e.getY() - 5, sliderBorrador.getValue(), sliderBorrador.getValue());

                    }
                    // de 25 a 39
                    if (sliderBorrador.getValue() >= 25 && sliderBorrador.getValue() < 40) {
                        circulo2d.fillOval(e.getX() - 10, e.getY() - 10, sliderBorrador.getValue(), sliderBorrador.getValue());
                    }
                    //de 40 a 59
                    if (sliderBorrador.getValue() >= 40 && sliderBorrador.getValue() < 60) {
                        circulo2d.fillOval(e.getX() - 15, e.getY() - 15, sliderBorrador.getValue(), sliderBorrador.getValue());
                    }
                    // de 60 a 79
                    if (sliderBorrador.getValue() >= 60 && sliderBorrador.getValue() < 80) {
                        circulo2d.fillOval(e.getX() - 25, e.getY() - 25, sliderBorrador.getValue(), sliderBorrador.getValue());
                    }
                    //de 80 a 89
                    if (sliderBorrador.getValue() >= 80 && sliderBorrador.getValue() < 90) {
                        circulo2d.fillOval(e.getX() - 35, e.getY() - 35, sliderBorrador.getValue(), sliderBorrador.getValue());
                    }
                    // de 90 a 100
                    if (sliderBorrador.getValue() >= 90) {
                        circulo2d.fillOval(e.getX() - 35, e.getY() - 35, sliderBorrador.getValue(), sliderBorrador.getValue());
                    }
                    guardaste=false;

                }
            }
        });

        //image = new BufferedImage(LienzoPanel.getWidth(), LienzoPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, LienzoPanel.getWidth(), LienzoPanel.getHeight());
        g.dispose();
        g.setStroke(new BasicStroke(brushWidth));
    }//GEN-LAST:event_BorradorBtnActionPerformed

    private void sliderBorradorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBorradorStateChanged
        alto = sliderBorrador.getValue();
        ancho = sliderBorrador.getValue();
        //brushWidth=sliderBorrador.getValue();//para el grosor del pincel por medio del Slider si gustan
    }//GEN-LAST:event_sliderBorradorStateChanged

    private void ColorLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ColorLblMouseClicked
        //cuando el usuario selecciona un color en el cuadro de diálogo, se actualiza el color del pincel
        //que se usa para dibujar en el panel de dibujo, y se actualiza el color del objeto Graphics2D
        //que se usa para dibujar en la imagen.
        Color newColor = JColorChooser.showDialog(null, "Seleccione un color", currentColor);//abre la ventana para cambiar el color
        if (newColor != null) {
            currentColor = newColor;// se actualiza el color
            textColor = currentColor; // se actualiza el color del texto
            Graphics2D g = image.createGraphics();
            g.setColor(currentColor);// lo setea al pincel
        }
    }//GEN-LAST:event_ColorLblMouseClicked

    private void cajaGrosorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cajaGrosorActionPerformed
        String tipoGrosor = (String) cajaGrosor.getSelectedItem();
        if (tipoGrosor.equals("chico")) {
            brushWidth = 1;
        }
        if (tipoGrosor.equals("mediano")) {
            brushWidth = 2;
        }
        if (tipoGrosor.equals("grande")) {
            brushWidth = 3;
        }
        if (tipoGrosor.equals("extra grande")) {
            brushWidth = 4;
        }
    }//GEN-LAST:event_cajaGrosorActionPerformed

    private void TextoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextoBtnActionPerformed
        seleccion = "texto";
        //vuelve visible las herramientas extra de texto
        fuenteBx.setVisible(true);
        tamanoBx.setVisible(true);
        NegritasBt.setVisible(true);
        CursivaBt.setVisible(true);
        trianguloBtn.setEnabled(false);
        lineaBtn.setEnabled(false);
        circuloBtn.setEnabled(false);
        cuadradoBtn.setEnabled(false);
        LienzoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }//GEN-LAST:event_TextoBtnActionPerformed

    private void PincelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PincelBtnActionPerformed
        seleccion = "pincel";
        //vuelve invisible las herramientas extra de texto
        fuenteBx.setVisible(false);
        tamanoBx.setVisible(false);
        NegritasBt.setVisible(false);
        CursivaBt.setVisible(false);
        trianguloBtn.setEnabled(false);
        lineaBtn.setEnabled(false);
        circuloBtn.setEnabled(false);
        cuadradoBtn.setEnabled(false);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ImageIcon icon =new ImageIcon(getClass().getResource("/imagenes/pinceCursorIcon.png"));
        //BufferedImage icon = ImageIO.read(getClass().getResource("/imagenes/pinceC.png"));
        //Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(icon, new Point(0, 0), "customCursor");
        Cursor cursor = toolkit.createCustomCursor(icon.getImage(), new Point(1, 1), "cursor");
        LienzoPanel.setCursor(cursor);
    }//GEN-LAST:event_PincelBtnActionPerformed

    //Abre el archivo y autoajusta el panel si es necesario
    private void abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
        chooser.setFileFilter(filtro);
        int seleccionFile = chooser.showOpenDialog(this);

        if (seleccionFile == JFileChooser.APPROVE_OPTION) {
            archivo = chooser.getSelectedFile(); //La variable ahora apunta al archivo elegido

            try {
                //contenido es una variable que sirve como intermedio para almacenar los bytes del archivo
                //En esta línea lo que se hace es inicializar la variable con el tamaño del archivo
                byte[] contenido = new byte[(int) archivo.length()];
                DataInputStream lector = new DataInputStream(new FileInputStream(archivo));
                lector.readFully(contenido);  //Aquí ya se está escribiendo en la variable
                //En este caso se usa el método readFully ya que este lee una cantidad especificada de bytes
                //le estamos pasando la variable contenido por lo que va a leer todo el contenido del archivo
                lector.close();

                ImageIcon imagen = new ImageIcon(contenido);

                //Dependiendo del tamaño de la imagen, es como se decide qué hacer con el tamaño del panel
                if (imagen.getIconHeight() <= LienzoPanel.getHeight() && imagen.getIconWidth() <= LienzoPanel.getWidth()) {
//1 LA IMAGEN TIENE BUEN TAMAÑO
                    //Si la imagen es más pequeña que el lienzo, solo se ajusta el tamaño del lienzo si es necesario
                    LienzoPanel.setSize(imagen.getIconWidth(), imagen.getIconHeight());
                    JLabel label = new JLabel(imagen);
                    JViewport viewport = LienzoPanel.getViewport();
                    // Agregar el JLabel al Viewport
                    viewport.add(label);
                    LienzoPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                    LienzoPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    // Actualizar el JScrollPane
                    LienzoPanel.revalidate();
                    LienzoPanel.repaint();
                } else {
//2 LA IMAGEN ES MUY GRANDE
                    int anchoLienzo = LienzoPanel.getWidth() - 15, altoLienzo = LienzoPanel.getHeight() - 15;
                    //El "-15" es un margen de error, para evitar que aparezcan los sliders
                    Image imagen2 = imagen.getImage(); //Se convierte la imagen de "icono" a "Image
                    Image imagenEscalada; //Sirve como variable intermedia para cambiarle el tamaño
                    //Resulta que al icono no se le puede cambiar el tamaño, por eso se debe convertir a "Image"
                    if (imagen.getIconWidth() > imagen.getIconHeight()) {
                        //Si el ancho de la imagen es mayor, la imagen se escala al panel respecto al ancho
                        imagenEscalada = imagen2.getScaledInstance(anchoLienzo, -1, Image.SCALE_SMOOTH);
                    } else if (imagen.getIconWidth() < imagen.getIconHeight()) {
                        //Si el alto de la imagen es mayor, la imagen se escala al panel respecto al alto
                        imagenEscalada = imagen2.getScaledInstance(-1, altoLienzo, Image.SCALE_SMOOTH);
                    } else //Si la imagen es cuadrada se escala igual
                    {
                        imagenEscalada = imagen2.getScaledInstance(anchoLienzo, altoLienzo, Image.SCALE_SMOOTH);
                    }

                    ImageIcon iconoFinal = new ImageIcon(imagenEscalada);
                    LienzoPanel.setSize(iconoFinal.getIconWidth(), iconoFinal.getIconHeight());

                    JLabel label = new JLabel(iconoFinal);

                    //LO SIGUIENTE ES NECESARIO PARA ACTUALIZAR EL PANEL
                    JViewport viewport = LienzoPanel.getViewport();
                    // Agregar el JLabel al Viewport
                    viewport.add(label);
                    // Actualizar el JScrollPane
                    LienzoPanel.revalidate();
                    LienzoPanel.repaint();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    
    }//GEN-LAST:event_abrirActionPerformed

    private void guardarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarComoActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
        chooser.setFileFilter(filtro);
        int seleccionFile = chooser.showSaveDialog(this);
        if (seleccionFile == JFileChooser.APPROVE_OPTION) {
            archivo = chooser.getSelectedFile();
            if (!archivo.getName().endsWith(".png") && !archivo.getName().endsWith(".jpg") && !archivo.getName().endsWith(".gif")) {
                archivo = new File(archivo.getAbsolutePath() + ".png");
                //Si no se ingresa una extensión, el archivo se guarda como jpg por defecto
            }

            //El timer está puesto para que no se guarde la imagen con el FileChooser Abierto y este no salga
            Timer timer = new Timer(600, e -> {
                guardarImagen(LienzoPanel, archivo, "GuardarComo");
            }
            );
            timer.setRepeats(false);
            timer.start();
        }
        guardaste=true;

    }//GEN-LAST:event_guardarComoActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        if (archivo != null) {
            Timer timer = new Timer(600, e -> {
                guardarImagen(LienzoPanel, archivo, "Guardar");
            }
            );
            timer.setRepeats(false);
            timer.start();
        } else {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
            chooser.setFileFilter(filtro);
            int seleccionFile = chooser.showSaveDialog(this);
            if (seleccionFile == JFileChooser.APPROVE_OPTION) {
                archivo = chooser.getSelectedFile();
                if (!archivo.getName().endsWith(".png") && !archivo.getName().endsWith(".jpg") && !archivo.getName().endsWith(".gif")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                    //Si no se ingresa una extensión, el archivo se guarda como jpg por defecto
                }

                //El timer está puesto para que no se guarde la imagen con el FileChooser Abierto y este no salga
                Timer timer = new Timer(1000, e -> {
                    guardarImagen(LienzoPanel, archivo, "GuardarComo");
                }
                );
                timer.setRepeats(false);
                timer.start();
            }
        }
        guardaste=true;
        
    }//GEN-LAST:event_guardarActionPerformed

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        Paint ventana = new Paint();
        ventana.setVisible(true);
    }//GEN-LAST:event_nuevoActionPerformed

    private void cerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarActionPerformed
        if(guardaste==false)
        {
        confirmar=JOptionPane.showInternalConfirmDialog(null,"Se detecto una actualizacion en su documento, ¿desea guardarlo?", "INFORMACIÓN",JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);  
             if (confirmar==0) // si es 0 significado que le dio a "si"
             {
              if (archivo != null) 
              {
                Timer timer = new Timer(600, e -> {
                guardarImagen(LienzoPanel, archivo, "Guardar");
                
                Menu menu = new Menu();
                menu.setVisible(true);
                menu = null;
                this.dispose();
                }
                );
                timer.setRepeats(false);
                timer.start();
              } else {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
            chooser.setFileFilter(filtro);
            int seleccionFile = chooser.showSaveDialog(this);
            if (seleccionFile == JFileChooser.APPROVE_OPTION) {
                archivo = chooser.getSelectedFile();
                if (!archivo.getName().endsWith(".png") && !archivo.getName().endsWith(".jpg") && !archivo.getName().endsWith(".gif")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                    //Si no se ingresa una extensión, el archivo se guarda como jpg por defecto
                }

                //El timer está puesto para que no se guarde la imagen con el FileChooser Abierto y este no salga
                Timer timer = new Timer(1000, e -> {
                    guardarImagen(LienzoPanel, archivo, "GuardarComo");
                    Menu menu = new Menu();
                    menu.setVisible(true);
                    menu = null;
                    this.dispose();
                 }
                    );
                timer.setRepeats(false);
                timer.start();
                    }
                }
                guardaste=true;   
             }else// si puso que "no"
             {
            Menu menu = new Menu();
            menu.setVisible(true);
            menu = null;
            this.dispose();  
             }    

   
        }
        else
        {
            Menu menu = new Menu();
            menu.setVisible(true);
            menu = null;
            this.dispose(); 
        }
    }//GEN-LAST:event_cerrarActionPerformed

    private void FormasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FormasBtnActionPerformed
        trianguloBtn.setEnabled(true);
        lineaBtn.setEnabled(true);
        circuloBtn.setEnabled(true);
        cuadradoBtn.setEnabled(true);
        //vuelve invisible las herramientas extra de texto
        fuenteBx.setVisible(false);
        tamanoBx.setVisible(false);
        NegritasBt.setVisible(false);
        CursivaBt.setVisible(false);
        seleccion = "figuras";
        LienzoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        

    }//GEN-LAST:event_FormasBtnActionPerformed

    private void fuenteBxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fuenteBxActionPerformed
        fuentes = (String) fuenteBx.getSelectedItem();// de lo que escoja se guarda en la variable para asi cambiar la fuente de la letra
    }//GEN-LAST:event_fuenteBxActionPerformed

    private void tamanoBxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tamanoBxActionPerformed
        String aux = (String) tamanoBx.getSelectedItem();// debemos guardar el numero consegido y para eso se debe convertir a string
        tamanoText = Integer.parseInt(aux);// luego se pasa a int y lo guardamos en la varible y asi cuando se realice otro texto salga con ese tamaño
    }//GEN-LAST:event_tamanoBxActionPerformed

    private void NegritasBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NegritasBtActionPerformed
        estiloText = Font.BOLD;
    }//GEN-LAST:event_NegritasBtActionPerformed

    private void CursivaBtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CursivaBtActionPerformed
        estiloText = Font.ITALIC;
    }//GEN-LAST:event_CursivaBtActionPerformed

    private void EscalaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EscalaBtnActionPerformed
    }//GEN-LAST:event_EscalaBtnActionPerformed

    private void lineaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineaBtnActionPerformed
        tipoFigura="linea";
    }//GEN-LAST:event_lineaBtnActionPerformed

    private void trianguloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trianguloBtnActionPerformed
        tipoFigura="triangulo";
    }//GEN-LAST:event_trianguloBtnActionPerformed

    private void cuadradoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cuadradoBtnActionPerformed
        tipoFigura="cuadrado";
    }//GEN-LAST:event_cuadradoBtnActionPerformed

    private void circuloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_circuloBtnActionPerformed
        tipoFigura="circulo";
    }//GEN-LAST:event_circuloBtnActionPerformed

    private void menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuMouseClicked
         if(guardaste==false)
        {
        confirmar=JOptionPane.showInternalConfirmDialog(null,"Se detecto una actualizacion en su documento, ¿desea guardarlo?", "INFORMACIÓN",JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);  
             if (confirmar==0) // si es 0 significado que le dio a "si"
             {
                      if (archivo != null) {
            Timer timer = new Timer(600, e -> {
            guardarImagen(LienzoPanel, archivo, "Guardar");
            }
            );
            timer.setRepeats(false);
            timer.start();
        } else {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de imagen", "jpg", "png", "gif");
            chooser.setFileFilter(filtro);
            int seleccionFile = chooser.showSaveDialog(this);
            if (seleccionFile == JFileChooser.APPROVE_OPTION) {
                archivo = chooser.getSelectedFile();
                if (!archivo.getName().endsWith(".png") && !archivo.getName().endsWith(".jpg") && !archivo.getName().endsWith(".gif")) {
                    archivo = new File(archivo.getAbsolutePath() + ".png");
                    //Si no se ingresa una extensión, el archivo se guarda como jpg por defecto
                }

                //El timer está puesto para que no se guarde la imagen con el FileChooser Abierto y este no salga
                Timer timer = new Timer(1000, e -> {
                    guardarImagen(LienzoPanel, archivo, "GuardarComo");
                 }
                    );
                timer.setRepeats(false);
                timer.start();
                    }
                }
                guardaste=true;
        Menu menu = new Menu();
        menu.setVisible(true);
        menu = null;
        this.dispose();
             }else// si puso que "no"
             {
              Menu menu = new Menu();
              menu.setVisible(true);
              menu = null;
              this.dispose();  
             }    
        }
        Menu menu = new Menu();
        menu.setVisible(true);
        menu = null;
        this.dispose();
        
        /*
        Menu Menu = new Menu();
        Menu.setVisible(true);

        Menu = null;
        this.dispose();*/
    }//GEN-LAST:event_menuMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Paint().setVisible(true);
            }
        });
    }

    private void guardarImagen(JScrollPane lienzo, File archivoImagen, String guardado) {
        try {
            // Obtener el tamaño del JScrollPane
            int anchoImagen = lienzo.getWidth();
            int altoImagen = lienzo.getHeight();

            // Crear una imagen temporal para almacenar la captura de pantalla
            BufferedImage imagen = new BufferedImage(anchoImagen, altoImagen, BufferedImage.TYPE_INT_RGB);

            // Obtener el área visible del JScrollPane
            Rectangle area = lienzo.getViewport().getViewRect();
            //ESTA ES LA PARTE DE LA CAPTURA, USA UNA CLASE YA EXISTENTE
            // Crear un robot para tomar la captura de pantalla
            Robot robot = new Robot();
            robot.setAutoWaitForIdle(true);

            // Tomar la captura de pantalla del JScrollPane
            BufferedImage captura = robot.createScreenCapture(new Rectangle(lienzo.getLocationOnScreen().x + area.x,
                    lienzo.getLocationOnScreen().y + area.y, area.width, area.height));

            // Dibujar la captura de pantalla en la imagen temporal
            imagen.getGraphics().drawImage(captura, 0, 0, null);

            //Diferencia entre "Guardar" y "Guardar Como..."
            switch (guardado) {
                case "Guardar": //Respeta el tipo de archivo de la imagen
                    if (archivo.getName().endsWith(".png")) {
                        ImageIO.write(imagen, "png", archivoImagen);
                    } else if (archivo.getName().endsWith(".jpg")) {
                        ImageIO.write(imagen, "jpg", archivoImagen);
                    } else if (archivo.getName().endsWith(".gif")) {
                        ImageIO.write(imagen, "gif", archivoImagen);
                    } else {
                        ImageIO.write(imagen, "png", archivoImagen);
                    }
                    break;
                case "GuardarComo":
                    // Guarda la imagen en formato png por default
                    ImageIO.write(imagen, "png", archivoImagen);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BorradorBtn;
    private javax.swing.JLabel ColorLbl;
    private javax.swing.JToggleButton CursivaBt;
    private javax.swing.JButton EscalaBtn;
    private javax.swing.JButton FormasBtn;
    private javax.swing.JPanel HerramientaExtraPanel;
    private javax.swing.JPanel HerramientaPanel;
    private javax.swing.JScrollPane LienzoPanel;
    private javax.swing.JPanel LienzoPanel1;
    private javax.swing.JToggleButton NegritasBt;
    private javax.swing.JButton PincelBtn;
    private javax.swing.JButton TextoBtn;
    private javax.swing.JMenuItem abrir;
    private javax.swing.JComboBox<String> cajaGrosor;
    private javax.swing.JMenuItem cerrar;
    private javax.swing.JButton circuloBtn;
    private javax.swing.JButton cuadradoBtn;
    private javax.swing.JComboBox<String> fuenteBx;
    private javax.swing.JMenuItem guardar;
    private javax.swing.JMenuItem guardarComo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JButton lineaBtn;
    private javax.swing.JLabel menu;
    private javax.swing.JMenuItem nuevo;
    private javax.swing.JSlider sliderBorrador;
    private javax.swing.JComboBox<String> tamanoBx;
    private javax.swing.JButton trianguloBtn;
    // End of variables declaration//GEN-END:variables
}
