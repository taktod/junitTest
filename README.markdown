# JunitTest

## 概要
Junitによるテストは便利なんですが、記述をするのが面倒なので、アノーテーションの定義でかたが着くようにしてみました。
基本的に入力を記述。出力と照らし合わせるor出力をDumpする。

この２つのみを目的にしてあります。

## 使い方
1. eclipseのプロジェクトにJunit4のライブラリを追加。
2. さらにjunitTest.jarも追加。
3. Entry用のクラスを１つ書いておく。
4. 任意の関数に@Junitと@Testのアノーテーションをつける。
   <pre>
   <code>@Junit({
   		@Test({"test", "true"})
   	})
   	public String doSomething(String str, boolean flg) {
   </code>
   </pre>
5. 3のクラスを実行する。
6. 標準出力に動作結果が出力される。
   <pre>check for class:(DevelopTestEntry) method:(doSomething)
   param : test true 
   assume : @dump result : test:true
   String
   test:true
   </pre>

追加したアノーテーションはそのまま入力データと出力結果の目安になります。

## アノーテーションの説明
3つのアノーテーションがあります。

* @Junit
	<pre>Junitによる動作確認対象となる関数に付加する定義
	({})で囲って内部にTestを定義します。
	</pre>
* @Test
	* init
		<pre>@Initの指定のあるコンストラクタの初期化パラメーターをこのテスト時のみ別の定義を利用します。
		入力内容は@Initのvalueと同じ
		</pre>
	* value
		<pre>入力パラメーターデータすべて文字列で記述します。
		{}で囲って複数要素を定義することも可能</pre>
	* assume
		<pre>応答としてあるべき形を指定する。</pre>
* @Init
	* value
		<pre>コンストラクタに付加する定義すべて文字列で記述します。
		{}で囲って複数要素を定義することも可能
		対象クラスインスタンスが必要になったときに、この定義をベースに仮動作させます。</pre>

###valueの定義データ
* 数字 <pre>"135"等文字として定義しておけば、関数の定義にあわせてnew Integer("135")といった形で初期化される。</pre>
* 文字 <pre>"Hello world"等文字として定義しておけばそのまま使われる。</pre>
* 配列 <pre>"フォーマット[要素1][要素2]..."の形で定義する。型はフォーマットよりも関数定義を優先する。</pre>
* List <pre>"フォーマット[要素1][要素2]..."の形で定義すると、そのArrayListになる。</pre>
* Set <pre>"フォーマット[要素1][要素2]..."の形で定義すると、そのHashSetになる。</pre>
* Map <pre>"フォーマット->フォーマット[key1->value1][key2->value2]..."の形で定義すると、そのHashMapになる。</pre>
* 任意のオブジェクト <pre>EntryクラスのsetUpメソッド内でsetData関数により特定のkeyに対応するvalueをセットしておくと、"#key"という形で利用できる。</pre>
* クラス <pre>"$java.lang.String"という風に$を頭にいれると対象クラスオブジェクトを渡すことができる。</pre>

<pre>フォーマット指定は
boolean, char*, byte, short, int, long, float, double, string
charは動作に問題があるはずなので使わないこと推奨</pre>

###assumeの定義データ
* @none <pre>出力データの確認なし。何があろうとそのままテストを実行する。</pre>
* @ok <pre>出力データの確認なし。例外が発生した場合はテストを中断する。</pre>
* @dump <pre>応答データを標準出力に出す。動作確認は行わない。(未定義のときのデフォルトの値)</pre>
* @pause <pre>メソッドの動作確認をしたあと、Enterキーの入力を待つ</pre>
* @dumppause <pre>出力をdumpした後にEnterキーの入力を待つ</pre>
* @dumpabort <pre>出力をdumpした後にEnterキーの入力をまって中断</pre>
* @abort <pre>Enterキーの入力をまって中断</pre>
* @xxxException <pre>例外が発生することを期待する。対象例外と一致する名前の例外が発生しない場合はテストを中断する。</pre>
* @custom[xxx] <pre>終了時にTestEntry継承クラス内のcustomCheck関数により判定する。true:処理続行 false:処理中断</pre>
* 文字列 <pre>応答データをtoStringで文字列化したときの値が一致することを期待する。それ以外の場合はエラー扱いでテスト中断。</pre>

## ライセンス
一応LGPLということにしておきます。なにか問題がでた場合は適宜変更する予定

## 履歴
* 2011.08.25 Junitについて知る。酔った勢いでライブラリをつくってみる。
* 2011.08.26 共有のためgithubに公開、会社でつかってみたところ初期化がだめすぎて、使えない。
* 2011.08.27 いろいろ機能を追加
* 2011.08.28 WindowsXPで動作するように動作を若干変更。また例外がJunitの例外になるように細工。
* 2011.08.30 Testアノーテーションにクラス初期化のパラメータ指定をいれれるようにした。
			 <br />assumeの定義にcustomを追加し、プロジェクト固有の判定を実施できるようにした。

## やることメモ
* ほかのプログラムを作成しつつ、なにか欲しい機能が増えたら互換性を残しつつバージョンをあげていく。
* char型の扱いまわりにバグがあるはずなので、対処しておく。
* 関数定義ごとにRunの対象を自動で増やせるようにしたい。