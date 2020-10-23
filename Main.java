import java.io.ByteArrayInputStream;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{

	boolean clearflag=false;//=ボタン入力後にが存在したまま数値を入力すると初期化するためのフラグ
	boolean writeflag =false;//数値を入力せずに(以外の記号を押せないようするためのフラグ
	boolean minusflag=false;
	boolean signflag=false;
	boolean rootflag=false;

	static final String title="電卓";
	String tmp="";
	String minusStr="";

	//電卓の表示部
	TextField tf1=new TextField("");

	//ボタンの数字
	String[] strNum= {"0","1","2","3","4","5","6","7","8","9"};
	String[] strSign= {"+","-","*","/","=","AC","(",")",".","DEL","+/-","√","mod"};
	String[] strSign1= {"+","-","×","÷","=","AC","(",")",".","DEL","+/-","√","mod"};
	//ボタンの宣言
	Button[] btnNum=new  Button[strNum.length];
	Button[] btnSign = new Button[strSign.length];
	//ラベルの宣言
	Label label1=new Label(title);
	//数字のボタンが押された場合のtf1に入力するためのメソッド
	void numClicked(int i) {
		writeflag=true;
		rootflag=true;
		signflag=false;
		if(clearflag) reset();
		tmp+=strNum[i];
		tf1.setText(formula(tmp));
	}

	String formula(String str) {
		String tmp =str;
		tmp=tmp.replace("#", "-");
		tmp=tmp.replace("*", "×");
		tmp=tmp.replace("/", "÷");
		tmp=tmp.replace("mod", " mod ");
		return tmp;
	}

	//記号のボタンが入力された場合のtf1に入力するためのメソッド
	void signClicked(int i) {
		if((i==6||writeflag)&&!clearflag) {
			if(!signflag||i==6||i==7) {
				tmp+=strSign[i];
			}
			else {
				tmp=tmp.substring(0,tmp.length()-1)+strSign[i];
			}
			if(i==0||i==1||i==2||i==3)signflag=true;
			minusflag=false;
			rootflag=false;
			minusStr=tmp;
			tf1.setText(formula(tmp));
		}
	}
	//初期化するためのメソッド
	void reset() {
		writeflag=false;
		label1.setText(title);
		tf1.setText("");
		clearflag=false;
		tmp="";
		minusflag=false;
		signflag=false;
		minusStr="";
		rootflag=false;
	}

	void del() {
		if(!tmp.isEmpty()&&!clearflag) {
			tmp=tmp.substring(0,tmp.length()-1);
			if(tmp.matches("^[+-]?(0|[1-9][0-9]*)(\\.0)?$")
			||tmp.matches("^[+-]?(0|[1-9]+\\d*)(\\.[0-9]*)?$")
			) {
				String t=tmp;
				minusStr="";
				tmp=t;
			}
			tf1.setText(formula(tmp));
		}
	}
	void minussign() {
		if(!clearflag) {
			if(!minusflag) {
				minusflag=true;
				/*
				System.out.println(minusStr);
				System.out.println(signflag);
				System.out.println(tmp.replace(minusStr, "#"));
				System.out.println(tmp);
				*/
				tmp= minusStr!=""?minusStr+tmp.replace(minusStr, "#"):"#"+tmp;
				tf1.setText(formula(tmp));
			}
			else {
				minusflag=false;
				StringBuffer sb=new StringBuffer(tmp);
				String tmp1=sb.reverse().toString();
				tmp1=tmp1.replace("#", "");
				sb=new StringBuffer(tmp1);
				tmp=sb.reverse().toString();
				tf1.setText(formula(tmp));
			}
		}
	}
	void equalClicked() {
		try {
			if(writeflag) {
				signClicked(4);
				clearflag=true;
				label1.setText(formula(tmp));
				final Parser parser =new Parser(new ByteArrayInputStream(tmp.getBytes("utf-8")));
				double d=parser.start();
				String str=Double.toString(d);
				if(str.matches("^[+-]?(0|[1-9][0-9]*)(\\.0)?$"))str=Integer.toString((int)d);
				else if(str.matches("^[+-]?(0|[1-9]+\\d*)(\\.[0-9]*)?$"))str=Double.toString(d);
				else str="計算エラー";
				tf1.setText(str);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			tf1.setText("構造エラー");
		}finally {
			writeflag=false;
		}
	}
	void rootClicked() {
		if(writeflag&&rootflag) {
			//rootflag=true;
			String str1=tmp.replace(minusStr, "");
			//System.out.println(str1);
			if(!str1.contains("#")) {
				double d=Math.sqrt(Double.parseDouble(str1));
				tmp=d==(int)d?minusStr+Integer.toString((int)d):minusStr+Double.toString(d);
			}
			else{
				tmp="虚数";
				clearflag=true;
			}
			tf1.setText(formula(tmp));
		}
	}

	@Override
	public void start(Stage primaryStage) throws ParseException{
		final int btnSpc=10;
		final int charSize=20;
		final int btnSize=40;

		//ラベルの設定
		label1.setFont(new Font(20));
		label1.setPrefSize(8*btnSize+3*btnSpc,30);
		label1.setAlignment(Pos.TOP_LEFT);

		//テキストフィールドの設定
		tf1.setEditable(false);
		tf1.setAlignment(Pos.BASELINE_RIGHT);
		tf1.setFont(new Font(25));
		tf1.setPrefWidth(8*btnSize+3*btnSpc);


		//数字ボタンの宣言
		for(int i=0;i<btnNum.length;i++) {
			btnNum[i]=new Button(strNum[i]);
			btnNum[i].setPrefSize(2*btnSize,btnSize);
			btnNum[i].setFont(new Font(charSize));
		}
		//数字ボタンが入力された場合の動作
		btnNum[0].setOnAction(e->numClicked(0));//0
		btnNum[1].setOnAction(e->numClicked(1));//1
		btnNum[2].setOnAction(e->numClicked(2));//2
		btnNum[3].setOnAction(e->numClicked(3));//3
		btnNum[4].setOnAction(e->numClicked(4));//4
		btnNum[5].setOnAction(e->numClicked(5));//5
		btnNum[6].setOnAction(e->numClicked(6));//6
		btnNum[7].setOnAction(e->numClicked(7));//7
		btnNum[8].setOnAction(e->numClicked(8));//8
		btnNum[9].setOnAction(e->numClicked(9));//9
		btnNum[0].setPrefSize(4*btnSize+btnSpc,40);//0ボタンだけ大きくする

		//記号ボタンの宣言
		for(int i=0;i<btnSign.length;i++) {
			btnSign[i]=new Button(strSign1[i]);
			btnSign[i].setPrefSize(2*btnSize,btnSize);
			btnSign[i].setFont(new Font(charSize));
		}
		//記号ボタンが入力された場合の動作
		btnSign[5].setFont(new Font(charSize-3));
		btnSign[0].setOnAction(e->signClicked(0));//+
		btnSign[1].setOnAction(e->signClicked(1));//-
		btnSign[2].setOnAction(e->signClicked(2));//*
		btnSign[3].setOnAction(e->signClicked(3));//÷
		btnSign[4].setOnAction(e->equalClicked());//=
		btnSign[5].setOnAction(e->reset());//AC
		btnSign[6].setOnAction(e->signClicked(6));//(
		btnSign[7].setOnAction(e->signClicked(7));//)
		btnSign[8].setOnAction(e->signClicked(8));//.
		btnSign[9].setOnAction(e->del());
		btnSign[10].setOnAction(e->minussign());
		btnSign[11].setOnAction(e->rootClicked());
		btnSign[12].setOnAction(e->signClicked(12));
		//ボタンの横一列を収納するHBox
		HBox[] hbBtns=new HBox[6];
		hbBtns[0]=new HBox(btnSpc,btnSign[9],btnSign[10],btnSign[11],btnSign[12]);
		hbBtns[1]=new HBox(btnSpc,btnSign[5],btnSign[6],btnSign[7],btnSign[3]);
		hbBtns[2]=new HBox(btnSpc,btnNum[7],btnNum[8],btnNum[9],btnSign[2]);
		hbBtns[3]=new HBox(btnSpc,btnNum[4],btnNum[5],btnNum[6],btnSign[1]);
		hbBtns[4]=new HBox(btnSpc,btnNum[1],btnNum[2],btnNum[3],btnSign[0]);
		hbBtns[5]=new HBox(btnSpc,btnNum[0],btnSign[8],btnSign[4]);
		//中央寄りにする
		for(int i=0;i<hbBtns.length;i++)
			hbBtns[i].setAlignment(Pos.CENTER);
		//ボタンの一列をVBoxで一つにまとめる
		VBox Btns =new VBox(btnSpc,hbBtns[0],hbBtns[1],hbBtns[2],hbBtns[3],hbBtns[4],hbBtns[5]);
		//ラベル
		HBox hb1=new HBox(btnSpc,label1);
		hb1.setAlignment(Pos.CENTER);
		//テキストボックス
		HBox hb2=new HBox(btnSpc,tf1);
		hb2.setAlignment(Pos.CENTER);

		//全てのレイアウト
		VBox vb =new VBox(btnSpc,hb1,hb2,Btns);
		primaryStage.setScene(new Scene(vb, 8*btnSize+4*btnSpc, 430));
        primaryStage.setTitle(title);
        primaryStage.show();
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Application.launch(args);
        System.out.println("完了--電卓--");
	}

}
