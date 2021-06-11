package MoGong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class productInfo extends JFrame implements ActionListener {

	private int res = 10, m;
	private JButton btnBuy, btnres;
	private JLabel lblimg, lblinfoimg, jungga, wait;
	private ImageIcon img, back, infoimg;
	private String itemname, itemprice, itembrand, itemclass, itemimage;
	private Object itemid;
	String id;

	public productInfo(String title, int width, int height, int n, String id) throws MalformedURLException {
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(this);
		this.id = id;
		// setLocation(250, 150);

		JPanel backpan = new JPanel();
		backpan.setBackground(Color.WHITE);
		JScrollPane scrollpane = new JScrollPane(backpan);

		backpan.setLayout(new BorderLayout());

		outputProductListSpec(n); // 선택한 상품 출력
		m = n; // 다음 buyframe 에 보낼 인자값 저장
		// 첫번째 팬 (사진넣기)
		JPanel pan1 = new JPanel();
		URL ImageURL = new URL("http:" + itemimage);
		img = new ImageIcon(ImageURL);
		lblimg = new JLabel(img);

		pan1.setOpaque(false);
		pan1.add(lblimg);
		backpan.add(pan1, BorderLayout.NORTH);

		// 두번째 팬 ( 가격정보 및 상품정보)
		JPanel pan2 = new JPanel();
		pan2.setLayout(new GridLayout(6, 1));
		DecimalFormat dc = new DecimalFormat("###,###,###"); // 가격에 반점 넣기

		JLabel name = new JLabel(itemname, SwingConstants.CENTER);

		String ch = dc.format(Integer.parseInt(itemprice)); // 반점넣기

		JLabel jungga = new JLabel(ch + "원", SwingConstants.CENTER);

		double saleprice = (double) ((Integer.parseInt(itemprice) * 0.9)); // 10%할인 적용
		ch = dc.format(saleprice);

		JLabel price = new JLabel(ch + "원 (10% SALE)", SwingConstants.CENTER); // 10%할인한 가격 넣기

		// 몇명 남았는지 표기 위한 DB
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@118.217.168.174:1521:xe", "comet", "1234");
			Statement stmt = conn.createStatement();
			Statement stmt2 = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT count(itemid) FROM orders WHERE itemid = '" + itemid + "' group by itemid");
			ResultSet rs2 = stmt2.executeQuery("SELECT count(state) FROM orders WHERE itemid = '" + itemid + "' AND STATE = 2");
			
			int state = 0, show = 0;
			
			if (rs2.next()) {
				state = rs2.getInt(1);
			}
			
			if (rs.next()) {
				//show가 값을 못가져오는 오류 발생 해결 아직 안됨
				show = rs.getInt(1);
				res = 10 - show + state;
			}
			conn.close();
		} catch (ClassNotFoundException e1) {
			System.out.println("JDBC드라이버 로드 에러");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.err.println("DB연결 오류 또는 쿼리 오류 입니다.");
			e1.printStackTrace();
		}

		// 몇명 남았는지 표기
		wait = new JLabel(" 남은 구매 예약자 : " + res + "명 ", SwingConstants.CENTER);
		pan2.add(name);
		pan2.add(jungga);
		pan2.add(price);
		pan2.add(wait);

		// 폰트설정
		name.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		jungga.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		price.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		price.setForeground(Color.RED);

		wait.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		wait.setForeground(Color.RED);

		JPanel subpan2_1 = new JPanel();

		btnBuy = new JButton("구매예약");
		btnBuy.addActionListener(this);

		btnres = new JButton("예약취소");
		btnres.addActionListener(this);
		btnres.setEnabled(false);

		subpan2_1.add(btnBuy);
		subpan2_1.add(btnres);
		subpan2_1.setOpaque(false);
		pan2.add(subpan2_1);

		pan2.setOpaque(false);

		// 상품정보 pan3
		JPanel pan3 = new JPanel();
		pan3.setLayout(new GridLayout());

		infoimg = new ImageIcon("images/info2.png");
		infoimg = imageSetSize(infoimg, 600, 2000);
		lblinfoimg = new JLabel(infoimg);

		pan3.add(lblinfoimg);
		pan3.setOpaque(false);

		backpan.add(pan2);
		backpan.add(pan3, BorderLayout.SOUTH);

		add(scrollpane);

		setVisible(true);

	}

	// public static void main(String[] args) throws MalformedURLException {

	// }

	private void outputProductListSpec(int n) {
		ProductDB db = new ProductDB();
		List<productdto> list = db.getProductList();

		// i는 5의배수로 넣을예정. 총 16개

		itemid = list.get(n).getItemid();
		itemname = list.get(n).getItemname();
		//
		itemprice = list.get(n).getItemprice();
		itembrand = list.get(n).getItembrand();
		itemclass = list.get(n).getItemclass();
		itemimage = list.get(n).getItemimage();

		itemprice = itemprice.replace(",", ""); // ,을 제거하여 나중에 int로 변환하여 할인하기 편하게함.

	}

	ImageIcon imageSetSize(ImageIcon icon, int i, int j) {
		Image ximg = icon.getImage();
		Image yimg = ximg.getScaledInstance(i, j, java.awt.Image.SCALE_SMOOTH);
		ImageIcon xyimg = new ImageIcon(yimg);
		return xyimg;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == btnBuy) {

			// 입력 DB 작성중
			/*try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@118.217.168.174:1521:xe", "comet",
						"1234");
				Statement stmt = conn.createStatement();

				ResultSet rs = stmt
						.executeQuery("SELECT count(itemid) FROM orders WHERE ITEMID = '" + itemid + "' group by");

				if (rs.next()) {
					res = 10 - rs.getInt(1);
				}
				conn.close();
			} catch (ClassNotFoundException e1) {
				System.out.println("JDBC드라이버 로드 에러");
				e1.printStackTrace();
			} catch (SQLException e1) {
				System.err.println("DB연결 오류 또는 쿼리 오류 입니다.");
				e1.printStackTrace();
			}*/

			btnres.setEnabled(true);
			btnBuy.setEnabled(false);

			wait.setText(" 남은 구매 예약자 : " + res + "명 ");

			// 프레임 띄우기
			try {
				BuyFrame bs = new BuyFrame("결제화면/" + id, 800, 800, m, id);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}

		} else if (obj == btnres) {
			btnres.setEnabled(false);
			btnBuy.setEnabled(true);

			// 삭제 DB 작성중
			/*try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@118.217.168.174:1521:xe", "comet",
						"1234");
				Statement stmt = conn.createStatement();

				//구매예약을 구매 취소로 바꾸는 쿼리
				stmt.executeQuery("DELETE FROM ORDERS WHERE ID = '" + id
						+ "' AND ITEMID = (SELECT itemid FROM item WHERE itemname = '" + itemname + "')");
				
				//res에 반영하는 쿼리
				ResultSet rs = stmt.executeQuery("DELETE FROM ORDERS WHERE ID = '" + id
						+ "' AND ITEMID = (SELECT itemid FROM item WHERE itemname = '" + itemname + "')");

				if (rs.next()) {
					res = 10 - rs.getInt(1);
				}
				conn.close();
			} catch (ClassNotFoundException e1) {
				System.out.println("JDBC드라이버 로드 에러");
				e1.printStackTrace();
			} catch (SQLException e1) {
				System.err.println("DB연결 오류 또는 쿼리 오류 입니다.");
				e1.printStackTrace();
			}*/

			// res++;
			wait.setText(" 남은 구매 예약자 : " + res + "명 ");
		}

	}

}