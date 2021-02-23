package aermod;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import calpuff.RoundedButton;

public class InputPanel extends JFrame implements PanelTemplete {
	private static final long serialVersionUID = 1L;
	public static final PanelTemplete CTGResultPanel = null;
	Map<String, PanelTemplete> frames;
	
	JFrame frame;
	private Color white = new Color(255,255,255);
	JPanel aerinjp = new JPanel();
	JLabel title = new JLabel();
	JLabel content = new JLabel();
	JLabel company = new JLabel();
	JLabel company_lat = new JLabel();
	JLabel company_lon = new JLabel();
	JTextField company_lat_txt = new JTextField();
	JTextField company_lon_txt = new JTextField();
	JLabel ksic = new JLabel();
	JLabel topy = new JLabel();
	JTextField topy_txt = new JTextField();
	JButton topy_load = new RoundedButton("파일 불러오기", Color.decode("#BF95BC"), white, 20);
	JLabel boundary = new JLabel();
	JTextField boundary_txt = new JTextField();
	JButton boundary_load = new RoundedButton("파일 불러오기", Color.decode("#BF95BC"), white, 20);
	JLabel source = new JLabel();
	JTextField source_txt = new JTextField();
	JButton source_load = new RoundedButton("파일 불러오기", Color.decode("#BF95BC"), white, 20);
	JButton next = new RoundedButton("다음", Color.decode("#BF95BC"), white, 20);
	
	public InputPanel(JFrame frame) {
		this.frame = frame;
	}
	
	public void setPanel() {
		
		aerinjp.setLayout(null);
		
		// 프레임
		aerinjp.add(title);
		
		
		aerinjp.add(company);
		aerinjp.add(company_lat);
		aerinjp.add(company_lat_txt);
		aerinjp.add(company_lon);
		aerinjp.add(company_lon_txt);
		
		
		aerinjp.add(ksic);
		
		aerinjp.add(topy);
		aerinjp.add(topy_txt);
		aerinjp.add(topy_load);
		aerinjp.add(boundary);
		aerinjp.add(boundary_txt);
		aerinjp.add(boundary_load);
		
		aerinjp.add(source);
		aerinjp.add(source_txt);
		aerinjp.add(source_load);
		
		aerinjp.add(next);
		
		aerinjp.add(content);
		
		title.setLocation(0, 0); title.setSize(1000, 100);
		
		content.setHorizontalAlignment(SwingConstants.CENTER);
		content.setVerticalAlignment(SwingConstants.CENTER);
		content.setOpaque(true);
		content.setBackground(Color.decode("#D0D8DA"));
		content.setLocation(0, 100); content.setSize(1000, 600);
		
		company.setLocation(100, 125); company.setSize(150, 50);
		company.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		company.setText("사업장 위치 정보 입력");
		company_lat.setLocation(300, 125); company_lat.setSize(50, 50);
		company_lat.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		company_lat.setText("위도");
		company_lat_txt.setLocation(350, 135); company_lat_txt.setSize(150, 30);
		company_lat_txt.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		company_lat_txt.setText("");
		company_lon.setLocation(500, 125); company_lon.setSize(100, 50);
		company_lon.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		company_lon.setText("  ,      경도");
		company_lon_txt.setLocation(600, 135); company_lon_txt.setSize(150, 30);
		company_lon_txt.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		company_lon_txt.setText("");
		
		ksic.setLocation(100, 200); ksic.setSize(150, 50);
		ksic.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		ksic.setText("산업 분류 선택");
		
		topy.setLocation(100, 310); topy.setSize(200, 30);
		topy.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		topy.setText("지형도(.dxf)");
		topy_txt.setLocation(300, 310); topy_txt.setSize(350, 30);
		topy_txt.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		topy_txt.setText("");
		topy_load.setLocation(700, 310); topy_load.setSize(150, 30);
		topy_load.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		topy_load.addActionListener(new loadListener());
		
		boundary.setLocation(100, 410); boundary.setSize(200, 30);
		boundary.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		boundary.setText("부지 경계(.dxf)");
		boundary_txt.setLocation(300, 410); boundary_txt.setSize(350, 30);
		boundary_txt.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		boundary_txt.setText("");
		boundary_load.setLocation(700, 410); boundary_load.setSize(150, 30);
		boundary_load.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		boundary_load.addActionListener(new loadListener());
		
		source.setLocation(100, 510); source.setSize(200, 30);
		source.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		source.setText("배출원 정보 입력(.csv)");
		source_txt.setLocation(300, 510); source_txt.setSize(350, 30);
		source_txt.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		source_txt.setText("");
		source_load.setLocation(700, 510); source_load.setSize(150, 30);
		source_load.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		source_load.addActionListener(new loadListener());
		
		next.setLocation(800, 570); next.setSize(150, 50);
		next.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		
	}
	
	public void setVisible() {
		frame.add(aerinjp);
		aerinjp.setVisible(true);
	}
	
	public void setUnVisible() {
		aerinjp.setVisible(false);
	}
	
	public void setFrames(Map<String, PanelTemplete> frames) {
		this.frames = frames;
	}
	// 파일 탐색창
	class loadListener implements ActionListener {
		
		JFileChooser chooser;
		
		loadListener() {
			chooser = new JFileChooser();
		}
		
		public void actionPerformed(ActionEvent e) {
			String folderPath = "";
	        
	        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); // 디렉토리 설정
	        chooser.setCurrentDirectory(new File("/")); // 현재 사용 디렉토리를 지정
	        chooser.setAcceptAllFileFilterUsed(true);   // Fileter 모든 파일 적용 
	        chooser.setDialogTitle("타이틀"); // 창의 제목
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 파일 선택 모드
	        
	        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt"); // filter 확장자 추가
	        chooser.setFileFilter(filter); // 파일 필터를 추가
	        
	        int returnVal = chooser.showOpenDialog(null); // 열기용 창 오픈
	        
	        if(returnVal == JFileChooser.APPROVE_OPTION) { // 열기를 클릭 
	            folderPath = chooser.getSelectedFile().toString();
	            if (e.getSource() == topy_load) {
	            	topy_txt.setText(folderPath);
				} else if (e.getSource() == boundary_load) {
	            	boundary_txt.setText(folderPath);
				} else if (e.getSource() == source_load) {
					source_txt.setText(folderPath);
				}
	        }else if(returnVal == JFileChooser.CANCEL_OPTION){ // 취소를 클릭
	            System.out.println("cancel"); 
	            folderPath = "";
	        }
		}
		
	}
	
	// 페이지 이동
class MoveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
//			if (e.getSource() == makegeo) {
//				frames.get("ctg").setUnVisible();
//				frames.get("make").setVisible();
//			} 
			
			
		}
	}

	@Override
	public void exet(AermodDTO data) {
		// TODO Auto-generated method stub
		
	}
}
