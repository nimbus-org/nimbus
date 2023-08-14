# ChangeLog
## Nimbus 1.2.5
### 概要

バグFIX、機能追加、機能改善、動作変更を行いました。

### バグFIX
1. SharedContextServletのkeyset画面のRemoveボタンのカラムが崩れる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/3
2. BeanFlowのWhile文、For分のContinue、Breakの動作不具合を修正
  https://github.com/nimbus-org/nimbus/pull/9
3. NimbusExternalizerServiceで、java.sql.Timestampが正しく直列化できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/14
4. AuthenticateInterceptorServiceでThreadContextに認証情報が設定されない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/19
5. DistributedSharedContextServiceのsave()中に、SharedContextServletの画面操作が固まる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/27
  https://github.com/nimbus-org/nimbus/pull/29
6. NimbusExternalizerServiceでClassオブジェクトを非直列化できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/31
7. Publisherで、Java7のソートアルゴリズム変更影響で、java.lang.IllegalArgumentExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/44
8. Publisherで、複数Connection生成時に、正しくサービスとして登録されない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/45
9. テストフレームワークで、見積り工数が０になる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/46
10. TestSwingRunnerで、テストケースがNGになると、再実行できなくなる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/63
11. テストリソースのみをリソースとしてコピーできない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/100
12. DeflateHttpServletResponseWrapperのdeflateLengthが設定できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/133
13. UDPのServerConnectionで大量送信すると、CPUが張り付いてしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/138
14. MessageForwardingServiceのフェイルオーバー時に、メッセージがロストする不具合を修正
  https://github.com/nimbus-org/nimbus/pull/141
15. MessageReceiverServiceの性能改善
  https://github.com/nimbus-org/nimbus/pull/151
16. multicastでの配信で、クラスタ化したコネクションを複数生成すると、メッセージを重複して受信してしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/153
17. SharedContextサービス起動時にClusterMemberの数とServerConnectionが認識しているClientの数が合わずに起動しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/160
18. beanflow 実行時に if文 test属性部分の評価で NoSuchPropertyException が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/162
19. DatabaseScheduleManagerServiceで、SCHEDULE_TABLEのROWVERSIONの型をINTEGERにすると、例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/168
20. JavaDBやPostgresでDatabaseScheduleManagerServiceが正しく動作しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/170
21. DistributedSharedContextServiceのtimeoutが正しく動作しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/176
22. ClientConnectionのclose()を呼んでも、isStartReceive()がfalseにならない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/178
23. TcpKeepAliveCheckerServiceのsetRequestBytes(byte[])で設定したバイトが使用されていない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/180
24. WebSocketKeepAliveCheckerServiceのsetRequestBytes(byte[])で設定したバイトが使用されていない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/181
25. FileAppenderWriterServiceで出力ファイルを指定した場合に対象ディレクトリが存在しないとエラーが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/183
26. 複数のBeanFlowの非同期呼び出しを待つ場合に、replay nameの指定のみでは待合せできない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/187
27. constructor要素の直下にifdef要素を指定しても、有効にならない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/190
  https://github.com/nimbus-org/nimbus/pull/209
28. GlobalUIDクラスの非直列化が遅い不具合を修正
  https://github.com/nimbus-org/nimbus/pull/195
29. Rehashが有効な複数台構成の分散共有コンテキストで1台停止した場合、他のサーバでStackOverflowが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/198
30. DefaultQueueServiceで、滞留が多くなると、引き抜き性能が悪化する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/200
31. SharedQueueにSharedContextTransactionManagerでトランザクション処理させて、rollbackしても、間に合わない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/203
  https://github.com/nimbus-org/nimbus/pull/204
32. テストフレームワークのHttpRequestActionServiceでレスポンスボディが無い場合にエラーになる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/207
33. テストフレームワークで同じアクションIDが存在する場合に正常に動作しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/213
34. テストフレームワークのTestRunnerでfinallyのアクションが実行されない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/215
35. DailyRollingFileAppenderWriterServiceで、日付以下のローリングができない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/218
36. DitributedSharedContextのReash処理で失敗する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/223
37. MBeanWatcherServiceのPeriodのCount属性が正しく機能しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/227
38. ClientBeanFlowInvokerFactoryServiceを使ってBeanFlowを複数リモート呼び出しして、同期待ちすると、エラーを吐かずに待ち続ける場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/234
39. ClusterClientConnectionFactoryServiceで、joinしていないClusterServiceを使うとNullPointerExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/242
40. ConnectionFactoryServiceのdisabledClient()で、宛先アドレス指定で無効化しても、あとから接続した送信先が無効化されない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/244
41. PaddingStringConverterで、isCountTwiceByZenkaku=trueの場合に、全角文字が混ざった文字をパースすると、StringIndexOutOfBoundsExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/247
42. FLVRecordReaderで、isNoLineBreak=trueの時、FLVの長さをバイト長ではなく、文字長で処理している不具合を修正
  https://github.com/nimbus-org/nimbus/pull/247
43. DefaultQueueServiceで、MaxThresholdSizeを指定すると、pushで無限待ちになる場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/253
  https://github.com/nimbus-org/nimbus/pull/267
  https://github.com/nimbus-org/nimbus/pull/359
44. HttpClientFactoryServiceで、自己証明書を持つサーバへのHTTPS通信を行うとエラーになる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/262
45. StoreCacheOverflowActionServiceを使って、あふれ制御が行われた時に、キャッシュを参照するとnullになることがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/269
46. BeanFlowを実行するとreload()が必ず呼び出されてしまう場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/273
47. SharedContextに、サーバキャッシュを設定している場合に更新と参照を同時に行うと、正しく更新されない場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/276
48. SerializableExternalizerServiceで、writeExternal(Object, OutputStream)を呼ぶと、OutputStreamが閉じられてしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/282
49. SharedContextServiceに、ClientCacheMapのみを設定し、クライアントモードからサーバモードに変わると、ClientCacheMapが有効になってしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/288
50. BeanExchangeConverterで、出力プロパティの型がプリミティブだった場合に例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/310
51. SharedContextのlockを並列分散でラッシュすると、タイムアウトが発生する場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/312
52. サービスのテンプレートを使ったときに、同じテンプレートを適用したサービスが複数存在すると、正しく動かない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/328
53. BeanTableViewを絞り込まずにnimp(view)を呼び出すとNullPointerExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/340
54. BeanFlowRestServerServiceを使って、パラメータをデータセットに適用できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/357
  https://github.com/nimbus-org/nimbus/pull/358
55. SharedCotextServiceのonUpdate処理でindexの更新処理が実行されていない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/369
56. SharedContextServiceでクライアントモードで、サーバの持っているインデックスを使った検索を行うと例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/371
57. DefaultQuerySearchManagerServiceで検索条件にnullを渡した場合にエラーが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/401
58. 同じ設定の異なるCipherCryptServiceで、暗号化/復号化すると例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/416
59. テストフレームワークでエラーが発生した場合にメッセージが画面に表示されないことがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/420
60. テストフレームワークでテストケースが終了する前にスタブが終了してしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/424
61. QueryDataSetでexecuteQuery()した後で、RecordにsetProperty()すると、例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/427
62. InterpreterActionServiceでリモートサーバ上でInterpreterを実行したい場合にNotSerializableExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/429/files
63. QueryDataSetのNestedRecordListQueryを指定すると、例外が発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/433
64. NestedRecordQueryおよびNestedRecordListQueryの、propertyNamesの指定の有無に関わらず、全量のスキーマ情報が出力される不具合を修正
  https://github.com/nimbus-org/nimbus/pull/437
65. テストFWのSwing画面でサービス定義ロード時にエラーが発生してもコンソールに出力されないことがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/442
66. DefaultPersistentManagerServiceのloadで、配列のClassを指定すると、無限ループになる不具合を修正
  https://github.com/nimbus-org/nimbus/pull/456
67. BeanExchangeConverterで、インデクサを使った、展開機能を使うと、ArrayIndexOutOfBoundsExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/464
68. BeanExchangeConverterに同じRecordListを渡して複数回convertするとrecordが増え続ける不具合を修正した
  https://github.com/nimbus-org/nimbus/pull/476
69. BeanFlowRestServerServiceでrefで参照したobject-def要素にcode属性が指定されていないと、NullPointerExceptionが発生する不具合を修正した
  https://github.com/nimbus-org/nimbus/pull/480
  https://github.com/nimbus-org/nimbus/pull/487
70. BeanExchangeCovnerterのsetPartPropertyMapping()で同じ入力プロパティに対して、複数の出力プロパティを設定すると正しく動作しない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/486
71. DefaultPropertySchemaで、制約式にValidatorのサービス名を指定すると、入力変換のConverterとして使われてしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/489
72. RequestConnectionFactoryServiceでrequestを送信する相手が存在しない場合に無限待ちになる場合がある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/493
73. テストFWの画面でシナリオ開始時に例外が発生するとシナリオが開始されたままになることがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/501
74. RecordクラスでreplaceRecordSchemaでエラーが発生することがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/517
75. CSVCompareEvaluateActionServiceで正しく比較出来ないことがある不具合を修正
  https://github.com/nimbus-org/nimbus/pull/521
76. NimbusExternalizerServiceで、Vectorを直列化できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/532
77. BeanFlowInvokerServerServiceで、ThreadContextにゴミが残る不具合を修正
  https://github.com/nimbus-org/nimbus/pull/550
78. KubernetesClusterServiceが、EKS上だとクラスタメンバを正しく認識できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/553
79. TestRunnerでサービス定義の記載がおかしい際にログが出力される前にプロセスが終了してしまう不具合を修正
  https://github.com/nimbus-org/nimbus/pull/554
80. PersistentManagerのloadQueryにて、埋め込めクエリに埋め込み先がない場合inputを渡すとエラーが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/558
81. TestRunnerでHtmlBeanFlowCoverageRepoterが出力できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/559
  https://github.com/nimbus-org/nimbus/pull/562
82. FileOperateActionServiceのLSでファイルが正しく取得できない不具合を修正
  https://github.com/nimbus-org/nimbus/pull/561
83. schemaが無いDataSetをジャーナルで出力しようとするとNullPointerExceptionが発生する不具合を修正
  https://github.com/nimbus-org/nimbus/pull/565

### 機能変更
1. WebSocket関連クラスを別リポジトリに分離した
  https://github.com/nimbus-org/nimbus/pull/88
  分離後のリポジトリ
  https://github.com/nimbus-org/nimbus-websocket
2. DefaultPersistentManagerのload時にTypes.BINARYでもInputStreamが返るようにした
  https://github.com/nimbus-org/nimbus/pull/97
3. ClusterServiceのメインハートビートのタイムアウトの計算に、HeartBeatResponseTimeoutを含めるようにした
  https://github.com/nimbus-org/nimbus/pull/131
4. DefaultPersistantManagerでNumber型カラムの値がNullの場合、nullを返すようにした
  https://github.com/nimbus-org/nimbus/pull/132
5. DefaultPropertySchema.getType()が、プロパティの型を定義していない場合は、nullを返す仕様となっていたのを、Object.classを返すようにした
  https://github.com/nimbus-org/nimbus/pull/174
6. ClusterServiceが既にjoinしている場合でも、例外を投げないようにした
  https://github.com/nimbus-org/nimbus/pull/185
7. 異なるスキーマのRecordでも、同じ値を持つならば、equals()=trueになるようにした
  https://github.com/nimbus-org/nimbus/pull/355
8. ClusterServiceをインタフェース化した
  https://github.com/nimbus-org/nimbus/pull/385
9. BeanExchangeConverterで、交換するプロパティが見つからない場合に、例外を投げないようにした
  https://github.com/nimbus-org/nimbus/pull/460
10. DataSetJSONConverterでRecordのプロパティ値がnullの場合に出力変換した結果の値を出力するようにした
  https://github.com/nimbus-org/nimbus/pull/473
  https://github.com/nimbus-org/nimbus/pull/474
11. テストフレームワークでテストをキャンセルする時に、finallyで指定したactionを実行するまえにテストスタブのリソースをダウンロードするようにした
  https://github.com/nimbus-org/nimbus/pull/491
12. CodeMasterNotifyActionServiceのサービス開始時に、ServerConnectionFactoryを必須ではないようにした
  https://github.com/nimbus-org/nimbus/pull/505
13. AuthenticateInterceptorServiceで、AuthenticateStoreをactivate()したときに、authenticatedInfoがnullの場合、セッションを作成しないようにした
  https://github.com/nimbus-org/nimbus/pull/507

### 機能追加
1. Java6をサポート
  https://github.com/nimbus-org/nimbus/pull/1
2. RecordListに、RandomAccessをimplements
  https://github.com/nimbus-org/nimbus/pull/6
3. SerializableExternalizerService、NimbusExternalizerServiceの性能改善
  https://github.com/nimbus-org/nimbus/pull/7
4. Nimbus2からのBeanflowの機能取り込み
  https://github.com/nimbus-org/nimbus/pull/12
  https://github.com/nimbus-org/nimbus/pull/28
5. 間引き配信を行うThinOutClientConnectionFactoryServiceを追加
  https://github.com/nimbus-org/nimbus/pull/21
6. ClientConnectionをラップするWrappedClientConnectionFactoryServiceを追加
  https://github.com/nimbus-org/nimbus/pull/21
7. Java9をサポート
  https://github.com/nimbus-org/nimbus/pull/33
8. ScheduleManagerServletの画面の改善
  https://github.com/nimbus-org/nimbus/pull/35
9. ローカルで送受信するPublisherを追加
  https://github.com/nimbus-org/nimbus/pull/38
10. Publisherを経由して、リモートのMessageWriterサービスに書き込むMessageWriterサービスを追加
  https://github.com/nimbus-org/nimbus/pull/39
11. Genericsに対応
  https://github.com/nimbus-org/nimbus/pull/41
  https://github.com/nimbus-org/nimbus/pull/155
12. テストフレームワークのシナリオグループのXMLにaction要素にretryInterval属性及びretryCount属性を記載できるようにした
  https://github.com/nimbus-org/nimbus/pull/47
13. バイナリファイル比較アクションを追加
  https://github.com/nimbus-org/nimbus/pull/49
14. RESTHttpRequestに対して、テストリソース内で、URLキーを指定して、setKey()できるようにした
  https://github.com/nimbus-org/nimbus/pull/53
15. JMSでMessageを送信するTestActionサービスを追加
  https://github.com/nimbus-org/nimbus/pull/55
16. TCPでTextベースのプロトコルをテストするスタブを追加
  https://github.com/nimbus-org/nimbus/pull/56
17. CSVCompareEvaluateActionServiceで、CSV要素内の特定文字列のみを無視する機能を追加
  https://github.com/nimbus-org/nimbus/pull/59
18. SCPClientFactoryのJsch実装を追加
  https://github.com/nimbus-org/nimbus/pull/64
19. SSHコマンドを実行するTestActionを追加
  https://github.com/nimbus-org/nimbus/pull/64
20. Gitからリソースを取得するTestResourceManagerを追加
  https://github.com/nimbus-org/nimbus/pull/66
21. TestFrameworkでクライアントのリソースをサーバにアップできる機能を追加した
  https://github.com/nimbus-org/nimbus/pull/72
22. DataSetServletRequestParameterConverterでmultipartリクエストをサポートした
  https://github.com/nimbus-org/nimbus/pull/74
23. テストリソースのresource、title、descriptionタグ内に、XMLコメントアウトを書けるようにした
  https://github.com/nimbus-org/nimbus/pull/76
24. BeanFlowでデフォルトのトランザクションタイムアウトを設定できるようにした
  https://github.com/nimbus-org/nimbus/pull/81
25. AuthenticateInterceptorServiceで、ログインパスとログアウトパスをそれぞれ複数指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/82/files
26. テストフレームワークのActionのステータスを画面に表示するようにした
  https://github.com/nimbus-org/nimbus/pull/86
27. HttpServletRequestCheckInterceptorServiceにMediaTypeのみでチェックできる機能を追加した
  https://github.com/nimbus-org/nimbus/pull/89
28. 入力ストリームから出力ストリームに書き出すBindingConverterを追加した
  https://github.com/nimbus-org/nimbus/pull/91
29. HttpClientFactoryで、HTTP応答ステータスに対して、例外をthrowする機能を追加した
  https://github.com/nimbus-org/nimbus/pull/93
30. TestSwingRunnerにTestReporterを実行する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/99
31. テストフレームワークでSVNからリソースを取得する機能を追加
  https://github.com/nimbus-org/nimbus/pull/103
32. メッセージダイジェスト文字列コンバータを追加
  https://github.com/nimbus-org/nimbus/pull/106
33. DefaultPropertySchemaのバリデーションで、任意のInterpreterが使えるようにした
  https://github.com/nimbus-org/nimbus/pull/107
34. TestFrameworkで実行結果ファイルからエビデンスファイルを生成する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/109
35. テストフレームワークでファイルの圧縮、解凍ができるアクションを追加した
  https://github.com/nimbus-org/nimbus/pull/111
36. ScriptEngineInterpreterServiceのコマンド実行機能を追加した
  https://github.com/nimbus-org/nimbus/pull/112
37. AuthenticateInterceptorでログアウトした時に、セッションを無効化する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/116
38. BeanFlowのCompilerでTest用Interpreter以外を指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/118
39. IPアドレスの範囲検証を行うValidatorを追加した
  https://github.com/nimbus-org/nimbus/pull/120
40. HttpやFTPなどの外部接続関連の例外の中に発生したサービスの情報を含めるようにした
  https://github.com/nimbus-org/nimbus/pull/125
45. BlockadeInterceptorでSession上のオブジェクトからspecialUserかどうか判断できるようにした
  https://github.com/nimbus-org/nimbus/pull/126
46. SharedContextにストアするAuthenticateStoreサービスを追加した
  https://github.com/nimbus-org/nimbus/pull/130
47. ConnectionFactoryServiceを生成するためのFactoryServiceを追加した
  https://github.com/nimbus-org/nimbus/pull/137
48. Input-defの属性を@@でアクセスできるようにした
  https://github.com/nimbus-org/nimbus/pull/143
49. サービス定義のテンプレート化における、テンプレート機能の改善
  https://github.com/nimbus-org/nimbus/pull/144
50. FilterとInterceptorにHTTPのメソッドで処理をフィルタする機能を追加した
  https://github.com/nimbus-org/nimbus/pull/147
51. リモートのサービス呼び出しをHTTPで行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/163
52. ifdef要素で指定した変数が存在するかどうかで条件判定できるようにした
  https://github.com/nimbus-org/nimbus/pull/165
53. ifdef要素にnot属性を追加した
  https://github.com/nimbus-org/nimbus/pull/166
54. VelocityTemplateEngineServiceのsetTemplateFileRootDirectory(File)の指定ディレクトリをサービス定義ファイルからの相対パスで指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/192
55. テストフレームワークにテキスト差分出力の機能を追加した
  https://github.com/nimbus-org/nimbus/pull/205
56. GCPのStackDriverに出力するCategoryとWriterサービスを追加した
  https://github.com/nimbus-org/nimbus/pull/208
57. MBeanWatcherServiceのJMX接続設定をMBeanServerConnectionFactoryに委譲できるようにした
  https://github.com/nimbus-org/nimbus/pull/211
58. パスワード認証を行うjavax.mail.Authenticatorを継承したサービスを追加した
  https://github.com/nimbus-org/nimbus/pull/220
59. OneWriteFileMessageWriterServiceで、存在しないディレクトリにファイルを出力できるようにした
  https://github.com/nimbus-org/nimbus/pull/222
60. MBeanServerRepositoryServiceで、PlatformMBeanServerを使えるようにした
  https://github.com/nimbus-org/nimbus/pull/225
61. サービス定義に、service-property要素を追加した
  https://github.com/nimbus-org/nimbus/pull/229
  https://github.com/nimbus-org/nimbus/pull/230
  https://github.com/nimbus-org/nimbus/pull/231
  https://github.com/nimbus-org/nimbus/pull/232
  https://github.com/nimbus-org/nimbus/pull/233
62. DefaultPropertySchemaでConverterありのParse時に自動で型変換できるようにした
  https://github.com/nimbus-org/nimbus/pull/236
63. DefaultPropertySchemaのConverterのクラス名の指定で、パッケージ指定がない場合、jp.ossc.nimbus.util.converterパッケージとみなすようにした
  https://github.com/nimbus-org/nimbus/pull/236
64. 改行が存在しないFLVデータも読み込めるようにした
  https://github.com/nimbus-org/nimbus/pull/238
65. BeanExchangeConverterで、一部のプロパティ交換だけ、指定する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/240
66. MBeanWatcherServiceで、チェック対象のリストを取得する機能を追加した。また、サービス及び監視対象に説明をつけることができるようにした
  https://github.com/nimbus-org/nimbus/pull/249
67. MBeanWatcherServiceで、チェック結果を監視結果として出力する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/251
68. DateFormatConverterで、Localeを設定する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/255
69. BeanExchangeConverterで、RecordからRecordListに変換する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/258
70. テストフレームワークで、RemoteServiceServerServiceをスタブ化する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/260
71. BeanFlowで、for、case、default、while、ifの子要素に、finally要素を書けるようにした
  https://github.com/nimbus-org/nimbus/pull/264
72. HttpClientFactoryServiceで、レスポンスの文字エンコード自動判別機能を実装した
  https://github.com/nimbus-org/nimbus/pull/266
73. ContextStoreのファイル実装を追加した
  https://github.com/nimbus-org/nimbus/pull/277
  https://github.com/nimbus-org/nimbus/pull/278
  https://github.com/nimbus-org/nimbus/pull/283
74. RemoteClientServiceで指定するRemoteServiceNameを動的に変更する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/280
75. AWS SQSをQueueサービスとして利用できるサービス実装を追加した
  https://github.com/nimbus-org/nimbus/pull/291
76. AWS lambdaを呼び出すScheduleExecutorサービス実装を追加した
  https://github.com/nimbus-org/nimbus/pull/293
  https://github.com/nimbus-org/nimbus/pull/305
77. BeanJSONConverterで、JSONをオブジェクトに変換する際に、変換先のプロパティの型がインタフェースでも変換できるようにした
  https://github.com/nimbus-org/nimbus/pull/295
78. DatabaseScheduleManagerServiceで、inputをJSONと文字列以外の変換もできるようにした
  https://github.com/nimbus-org/nimbus/pull/298
79. AWS SageMakerを呼び出すScheduleExecutorを追加した
  https://github.com/nimbus-org/nimbus/pull/300
  https://github.com/nimbus-org/nimbus/pull/301
80. BeanExchangeConverterで、setterやgetterがオーバーロードされていて型が判別できない場合に、型を明示する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/304
81. BeanJSONConverterで、setterやgetterがオーバーロードされていて型が判別できない場合に、型を明示する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/304
82. AWS Glueを呼び出すScheduleExecutorを追加した
  https://github.com/nimbus-org/nimbus/pull/306
83. AWS CloudWatch Logsに出力するMessageWriterを追加した
  https://github.com/nimbus-org/nimbus/pull/307
84. AWSCloudWatchメトリクスに書き込むMessageWriterを追加した
  https://github.com/nimbus-org/nimbus/pull/311
85. DataSetで、見かけ上のスキーマを設定する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/315
86. BeanFlowRestServerServiceで、requestやresponseのcode属性で、DTOのクラス名ではなく、DTOを生成するフロー名を指定する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/320
87. DataSetXpathConverterにDocumentBuilderのクラス名を指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/322
89. DataSetHtmlConverterをHTML5対応した
  https://github.com/nimbus-org/nimbus/pull/324
90. BlockadeInterceptorServiceでSession上のオブジェクトから閉塞チェックできるようにした
  https://github.com/nimbus-org/nimbus/pull/326
91. AuthenticateInterceptorServiceでLoginPathを指定しない場合も動くようにした
  https://github.com/nimbus-org/nimbus/pull/330
92. DefaultQuerySearchManagerServiceで、コンクリートでないRecordやRecordListを使えるようにした
  https://github.com/nimbus-org/nimbus/pull/332
93. StringEditConverterで、文字列をスプリットして取り出す機能を追加した
  https://github.com/nimbus-org/nimbus/pull/336
  https://github.com/nimbus-org/nimbus/pull/343
94. DataSetXpathConverterが、bindしたDataSetをcloneするか選択できるようにした
  https://github.com/nimbus-org/nimbus/pull/338
95. DefaultPropertySchemaの制約に、入力変換種類のようにValidatorを指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/346
96. 日付や数値のフォーマットを指定できるValidator実装を追加した
  https://github.com/nimbus-org/nimbus/pull/348
97. BeanFlowのinterperter要素の中で、他のbeanflowを呼び出せるようにした
  https://github.com/nimbus-org/nimbus/pull/350
98. RecordのString型のプロパティに対して、プリミティブ型の値をsetParseProperty()できるようにした
  https://github.com/nimbus-org/nimbus/pull/352
  https://github.com/nimbus-org/nimbus/pull/353
99. Cryptに、byte[]を暗号化/復号化するメソッドを追加した
  https://github.com/nimbus-org/nimbus/pull/362
100. DistributedSharedContextServiceの並列処理で、ThreadContextを使えるようにした
  https://github.com/nimbus-org/nimbus/pull/364
101. 暗号化されたサービス定義を読込みできるようにした
  https://github.com/nimbus-org/nimbus/pull/368
102. BeanFlowRestServerServiceのサーバ定義XMLで、DataSetを定義できるようにした
  https://github.com/nimbus-org/nimbus/pull/373
103. BeanFlowRestServerServiceのサーバ定義XMLで、requestやresponseのオブジェクトを共有できるようにした
  https://github.com/nimbus-org/nimbus/pull/377
104. BeanFlowRestServerServiceのサーバ定義XMLのobject-defで、定義の継承をできるようにした
  https://github.com/nimbus-org/nimbus/pull/379
105. QueryDataSetで、ネストしたレコードやレコードリストにもクエリを指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/381
106. DataSourceConnectionFactoryServiceで、データソース名の一部を動的に指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/383
107. Authrizationヘッダを解釈して、入力DTOに変換するConverterを追加した
  https://github.com/nimbus-org/nimbus/pull/387
108. CodeMasterServiceをリモートから同期更新する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/389
109. RequestMessageListenerを登録せずに、RequestConnectionFactoryServiceを使う場合には、サービス定義で、ResponseSubject属性に、応答サブジェクトを設定しておくことで、応答を受け取れるようにした
  https://github.com/nimbus-org/nimbus/pull/391
110. CodeMasterNotifyBeanにメッセージ作成とリクエストが同時にできる機能を追加した
  https://github.com/nimbus-org/nimbus/pull/393
111. Databaseのテーブルの内容をファイルに出力する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/395
112. MessageRecordFactoryServiceで、メッセージ定義ファイルのパスをサービス定義ファイルからの相対パスで指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/397
113. サービス定義のinvoke要素で、別サービスのメソッドをInvokeできるようにした
  https://github.com/nimbus-org/nimbus/pull/403
114. K8SのPod上で動作するClusterServiceを追加した
  https://github.com/nimbus-org/nimbus/pull/405
115. テンプレートのサービス定義ファイルをディレクトリ指定で読み込めるようにした
  https://github.com/nimbus-org/nimbus/pull/407
116. MessageDigestStringConverterでストレッチングの回数を指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/409
117. Cryptサービスで、IVを必要とする暗号化を使用する場合に、IVを毎回変えれるようにした
  https://github.com/nimbus-org/nimbus/pull/411
118. BeanFlowのtemplate要素で、テンプレートを別ファイルで定義できるようにした
  https://github.com/nimbus-org/nimbus/pull/413
119. コマンド実行機能を持つサービスや、コンパイル機能で、サービス定義ファイルのディレクトリを指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/418
120. ThymeleafのTemplateEngine実装を追加した
  https://github.com/nimbus-org/nimbus/pull/421
121. FreeMarkerのTemplateEngine実装を追加した
  https://github.com/nimbus-org/nimbus/pull/421
122. EvaluateCategoryServiceに、機能追加した
  https://github.com/nimbus-org/nimbus/pull/425
123. QueryDataSetを直列化できるようにした
  https://github.com/nimbus-org/nimbus/pull/431
124. BeanExchangeConverterで、異なる配列型のプロパティの交換ができるようにした
  https://github.com/nimbus-org/nimbus/pull/435
125. BeanExchangeConverterで、ネストしたDataSetへ変換できるようにした
  https://github.com/nimbus-org/nimbus/pull/439
  https://github.com/nimbus-org/nimbus/pull/440
126. BeanExchangeConverterで、インデックス付きアクセサに対応した
  https://github.com/nimbus-org/nimbus/pull/444
127. BeanExchangeConverterで、setPartPropertyMapping()で、同じ出力プロパティに対して複数のマッピングを設定できるようにした
  https://github.com/nimbus-org/nimbus/pull/446
128. QueryDataSetで、絞り込んだデータセットのネストスキーマを出力しないようにした
  https://github.com/nimbus-org/nimbus/pull/448
129. ジャーナルに同じ例外のスタックトーレスが何回も出ないようにする機能を追加した
  https://github.com/nimbus-org/nimbus/pull/450
130. TableCreatorServiceを操作するためのTestActionを追加した
  https://github.com/nimbus-org/nimbus/pull/452
131. CustomConverterに、配列の要素に対して変換を行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/454
132. ObjectJournalEditorServiceで、配列を編集する時に、出力する配列長を指定できるようにした
  https://github.com/nimbus-org/nimbus/pull/458
133. CipherCryptServiceで証明書の公開鍵を使用してVerifyが行えるようにした
  https://github.com/nimbus-org/nimbus/pull/465
134. DataSetJSONConverterでキャメルケースとスネークケースのプロパティ名を意識せずに変換できるようにした
  https://github.com/nimbus-org/nimbus/pull/467
135. BeanExchangeConverterで、特定のプロパティ間の交換の際に、変換を行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/469
136. RemoteServiceTestStubServiceでByte配列を文字列に変換してcllファイルを出力できるようにした
  https://github.com/nimbus-org/nimbus/pull/471
137. DataSetJSONConverterでキャメルケースとスネークケースのプロパティを変換できる機能を追加した
  https://github.com/nimbus-org/nimbus/pull/478
138. NumberValidatorで整数部、小数部の桁数チェックを行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/482
139. VelocityTemplateEngineServiceで、Velocity Toolを使えるようにした
  https://github.com/nimbus-org/nimbus/pull/485
140. Kubernetes Java Client 6.0.1まで対応
  https://github.com/nimbus-org/nimbus/pull/495
141. KubernetesClusterServiceで、クラスタメンバになり得るポッドが分かるようにした
  https://github.com/nimbus-org/nimbus/pull/497
  https://github.com/nimbus-org/nimbus/pull/498
  https://github.com/nimbus-org/nimbus/pull/499
142. HttpServletRequestTransferInterceptorServiceで、コードマスタから値を引く機能を追加した
  https://github.com/nimbus-org/nimbus/pull/503
143. AWS LambdaでBeanFlowを呼ぶRequestHandlerを追加した
  https://github.com/nimbus-org/nimbus/pull/511
144. システムプロパティの置換で、システムプロパティになければ環境変数も参照するようにした
  https://github.com/nimbus-org/nimbus/pull/513
145. ThreadManagedJournalServiceで、書き出しを同期的に行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/527
146. LogServiceで、ログの書き出しを同期的に行う機能を追加した
  https://github.com/nimbus-org/nimbus/pull/528
147. JSONのパースで、不要な空白文字を許容するようにした
  https://github.com/nimbus-org/nimbus/pull/534
148. AWS SQS経由で、リモートのサービスを非同期呼び出しする機能を追加した
  https://github.com/nimbus-org/nimbus/pull/536
149. ClusterConnectionFactoryServiceで、ClusterService以外のClusterインタフェース実装クラスを使用できるようにした
  https://github.com/nimbus-org/nimbus/pull/538
150. MasterValidatorServiceで、nullを検証するときに、マスタにnullがなくても、検証結果をtrueになるようにする機能を追加した
  https://github.com/nimbus-org/nimbus/pull/540
151. BeanExchangeConverterに、setPartPropertyMappings(Map)を追加した
  https://github.com/nimbus-org/nimbus/pull/542
152. ServiceManagerFactoryServletで、サービスがロードし終わったかどうかを確認する機能を追加した
  https://github.com/nimbus-org/nimbus/pull/546
153. サービス定義で、環境変数が参照できるようにした
  https://github.com/nimbus-org/nimbus/pull/548
154. OAuth2を使った、イントロスペクトを行うInterceptorを追加した
  https://github.com/nimbus-org/nimbus/pull/566
155. Kubernetes Java Client 7.0.0まで対応
  https://github.com/nimbus-org/nimbus/pull/568
156. Kuberneteseのコントロールプレーンを呼び出すScheduleExecutorを追加した
  https://github.com/nimbus-org/nimbus/pull/573

## Nimbus 1.2.4

### 概要

バグFIX、機能追加、機能改善、動作変更を行いました。

### バグFIX

1. jp.ossc.nimbus.beans.ConcatenatedPropertyの不具合を修正
  * getPropertyName()、toString()で、NullPointerExceptionが発生する場合がある不具合を修正した。
  * setAsText("")すると、長さ0のString配列がsetValue()されてしまう不具合を修正した。
2. jp.ossc.nimbus.beans.OrPropertyの不具合を修正
  * getPropertyName()、toString()で、NullPointerExceptionが発生する場合がある不具合を修正した。
3. jp.ossc.nimbus.beans.MapEditorの不具合を修正
  * 編集対象のMapの値がStringではない場合に、getAsText()を呼ぶとClassCastExceptionが発生する不具合を修正した。
4. jp.ossc.nimbus.beans.NimbusPropertyEditorManagerの不具合を修正
  * PropertyEditorManager.findEditor(Class)を並列で呼び出すと、ThreadがブロックされてしまうJavaの不具合を回避するように修正した。
5. jp.ossc.nimbus.beans.BeanTableIndexの不具合を修正
  * 内部クラスDefaultBeanTableIndexKeyFactoryの非直列化時に、InvalidClassExceptionが発生する不具合を修正した。
6. jp.ossc.nimbus.beans.BeanTableIndexManagerの不具合を修正
  * 直列化すると、インデックス情報が壊れて、検索できなくなる不具合を修正した。
7. jp.ossc.nimbus.beans.dataset.DataSetCodeGeneratorの不具合を修正
  * 複数階層継承したHeaderクラスを定義すると、クラス内に宣言されるPROPERTY_INDEX_OFFSETフィールドの値が正しくなくなる不具合を修正した。
8. jp.ossc.nimbus.beans.dataset.Recordの不具合を修正
  * RecordListにインデックスを設定し、追加したRecordのプロパティを変更すると、インデックスが正しく動作しない不具合を修正した。
9. jp.ossc.nimbus.core.DefaultServiceManagerServiceの不具合を修正
  * 依存するサービスが起動に失敗しても、サービスの開始が呼び出される場合がある不具合を修正した。
  * attribute要素のnullValue属性が正しく動作しない不具合を修正した。
  * getWaitingServices()、getFailedServices()の戻り値が直列化できない不具合を修正した。
10. jp.ossc.nimbus.io.CSVReaderの不具合を修正
  * cloneReader()で、setUnescapeLineSeparatorInEnclosure(boolean)の設定値が引き継がれない不具合を修正した。
11. jp.ossc.nimbus.recset.SchemaManagerの不具合を修正
  * findRowSchema(String)で、同じスキーマなのに、毎回違うインスタンスが生成されてしまう不具合を修正した。
12. jp.ossc.nimbus.service.aop.DefaultInterceptorChainFactoryServiceの不具合を修正
  * InterceptorChainCacheMapServiceNameを設定するとClassCastExceptionが発生する不具合を修正した。
13. jp.ossc.nimbus.service.aop.SelectableServletFilterInterceptorChainListServiceの不具合を修正
  * ServletPath属性がnullで、PathInfo属性がnullでない場合に、EnabledPathMapping属性が正しく動作しない不具合を修正した。
14. jp.ossc.nimbus.service.aop.servlet.AuthenticateInterceptorServiceの不具合を修正
  * 認証されているかチェックした時に、セッションタイムアウトしている場合に、NullPointerExceptionが発生する不具合を修正した。
15. jp.ossc.nimbus.service.aop.servlet.HttpServletResponseDeflateInterceptorServiceの不具合を修正
  * Accept-Encodingが不正な場合にNullPointerExceptionが発生する不具合を修正した。
16. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccessImpl2の不具合を修正
  * サービス定義からロードせずにサービスを起動すると、NullPointerExceptionが発生する不具合を修正した。
  * test属性で、stepの結果に対して、resultから始まるプロパティを参照すると、resultの省略ができない不具合を修正した。
  * catch要素内に、return、continue、breakを記述しても正しく動作しない不具合を修正した。
  * reply要素で応答待ちをして、例外が発生した後に、もう一度reply要素で応答待ちをすると、次の応答待ちを正しく行えない不具合を修正した。
  * callflow要素下のcatch要素下で、continue要素、break要素が正しく動作しない不具合を修正した。
17. jp.ossc.nimbus.service.beancontrol.DefaultBeanFlowInvokerFactoryServiceの不具合を修正
  * サービス定義からロードせずにサービスを起動すると、NullPointerExceptionが発生する不具合を修正した。
18. jp.ossc.nimbus.service.connection.DefaultPersistentManagerServiceの不具合を修正
  * load()時に、outputとして、Integer.classを指定すると、PersistentExceptionが発生する不具合を修正した。
  * 配列にloadする場合で、結果が1件の時、長さ1でnullが入った配列が戻る不具合を修正した。
  * load()で検索結果が0件の時に、nullが返らない場合がある不具合を修正した。
19. jp.ossc.nimbus.service.connection.TableCreatorServiceの不具合を修正
  * setDeleteOnStart(true)かつsetDropTableOnStart(true)の場合に、サービス開始時に例外が発生する不具合を修正した。
20. jp.ossc.nimbus.service.connection.TransactionSynchronizerServiceの不具合を修正
  * setDeleteOnSynchronize(false)の場合に、同期済みと未同期のレコードが混在する場合に、正しく動作しない不具合を修正した。
21. jp.ossc.nimbus.service.context.DatabaseContextStoreServiceの不具合を修正
  * サービスの停止時に、60秒かかる場合がある不具合を修正した。
22. jp.ossc.nimbus.service.context.SharedContextRecordListの不具合を修正
  * updateRemove(Record record, SharedContextValueDifference diff)で、複数件数削除すると、正しく削除できない不具合を修正した。
  * 差分更新時に、updateRemove、updateAddなど、リストの件数が増減する差分更新を複数同時に行えない不具合を修正した。
23. jp.ossc.nimbus.service.context.SharedContextServiceの不具合を修正
  * lock()メソッドで、同一サーバ上で、異なるスレッドで、同じキーのロックを行った場合、ロック待ちが発生せずに、SharedContextTimeoutExceptionが発生する不具合を修正した。
  * リモートで更新が発生した場合に、UpdateListener.onUpdateAfter()を呼び出すが、その際に、ClassCastExceptionが発生してしまう不具合を修正した。
  * lock()呼び出し時に、予期しないSharedContextTimeoutExceptionが発生する場合がある不具合を修正した。
  * SharedContextUpdateListener#onChangeMain()、onChangeSub()が正しく呼び出されない不具合を修正した。
  * update時に、更新バージョンが合わない場合に、ClassCastExceptionが発生する不具合を修正した。
  * 同期時に、ロックが残る場合がある不具合を修正した。
  * 他ノードがつなぎに来る前に同期しようとして、相手が見つからずに同期できない場合がある不具合を修正した。
  * サービスの開始時に他ノードも起動中の場合に、他ノードとの接続が正しく行えずに、正常に起動できない場合がある不具合を修正した。
  * サービスの開始時に他ノードも起動中の場合に、デッドロックを起こして、正常に起動できない場合がある不具合を修正した。
  * サーバモードで、putとgetが並列に処理されている場合に、putされているのに、ContextStoreのload()が実行される場合がある不具合を修正した。
  * CacheMapを設定して、analyzeIndex(String)を呼び出すと、例外が発生する不具合を修正した。
  * ContextStoreを設定し、nullの値をputして、getLocal(Object)すると無限ループが発生する不具合を修正した。
  * Mainでないノードで、lock()しようとした時に、ローカルのロックがロックされていると、待たずにタイムアウトしてしまう不具合を修正した。
24. jp.ossc.nimbus.service.context.SharedContextTransactionManagerServiceの不具合を修正
  * get()で、トランザクションのコンテキストに、取得対象のSharedContextValueDifferenceSupportの複製を保持して、1トランザクション中に、複数の更新を行えない不具合を修正した。
  * トランザクション中にremoveして、再度、updateやgetを行うと、SharedContextから再度取得してしまう不具合を修正した。
  * put()の戻り値が、put以前の値ではなく、putされた値のcloneになっている不具合を修正した。
25. jp.ossc.nimbus.service.context.DistributedSharedContextServiceの不具合を修正
  * SharedContextUpdateListener#onChangeMain、onChangeSubが正しく呼び出されない不具合を修正した。
  * クライアントモードのコンテキストのインデックスが、起動時に生成されない不具合を修正した。
  * サービスの開始時に、他ノードからリハッシュが要求されると、NullPointerExceptionが発生する場合がある不具合を修正した。
  * setMainDistributed(true)の場合で、クライアントモードのノードが混じると、メインノードがきれいに分散しない不具合を修正した。
  * CacheMapを設定して、analyzeIndex(String)を呼び出すと、例外が発生する不具合を修正した。
26. jp.ossc.nimbus.service.context.SharedContextIndexの不具合を修正
  * 内部クラスDefaultBeanTableIndexKeyFactoryの非直列化時に、InvalidClassExceptionが発生する不具合を修正した。
27. jp.ossc.nimbus.service.ftp.ftpclient.FTPClientFactoryServiceの不具合を修正
  * setSoTimeout(int)、setSoLinger(int)、setTcpNoDelay(boolean)を設定して、createFTPClient()を呼び出すと、NullPointerExceptionが発生する不具合を修正した。
  * active()を呼び出すと、例外が発生する不具合を修正した。
28. jp.ossc.nimbus.service.ga.DefaultConvergenceConditionServiceの不具合を修正
  * permissibleRelativeErrorを設定していて適応値の差と現世代の適応値の符号が一致しない場合に正しく収束しない不具合を修正した。
29. jp.ossc.nimbus.service.ga.DefaultGenerationの不具合を修正
  * compete(int threadNum, long timeout)呼び出し時に、timeoutに指定した時間を超えた場合に、タイムアウトしない場合がある不具合を修正した。
  * compete()実行時に、60秒かかる場合がある不具合を修正した。
30. jp.ossc.nimbus.service.http.proxy.ProxyServerServiceの不具合を修正
  * サービスの停止時に、60秒かかる場合がある不具合を修正した。
31. jp.ossc.nimbus.service.io.SerializableExternalizerServiceの不具合を修正
  * 圧縮モードを利用していて、圧縮されなかった場合に、非直列化に失敗する不具合を修正した。
32. jp.ossc.nimbus.service.jmx.MBeanWatcherServiceの不具合を修正
  * サービス停止時に、JMX接続の切断に失敗すると、停止できない不具合を修正した。
  * ログ出力時に、NUllPointerExceptionが発生する場合がある不具合を修正した。
  * 定期監視で、監視間隔が、一定にならない不具合を修正した。
33. jp.ossc.nimbus.service.journal.JournalRecordImplの不具合を修正
  * toObject(null)で呼び出すと、NullPointerExceptionが発生する不具合を修正した。
34. jp.ossc.nimbus.service.journal.RequestJournalImplの不具合を修正
  * setEndTime(Date)が呼び出される前に、getPerformance()を呼び出すと、NullPointerExceptionが発生する不具合を修正した。
35. jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderServiceの不具合を修正
  * findEditor(Object, Class)で、NullPointerExceptionが発生する場合がある不具合を修正した。
36. jp.ossc.nimbus.service.keepalive.ClusterServiceの不具合を修正
  * サービスの停止時にデッドロックする可能性がある不具合を修正した。
  * クライアントモードで、メインからハートビートが来た時に、参加要求を出してしまう不具合を修正した。
  * 通信相手が複数存在し、Windowの分割が発生した場合に、通信内容が混線する不具合を修正した。
  * leave()を呼び出した時に、ローカルのメンバ変更イベントが発生しない不具合を修正した。
  * join()した後にaddClusterListener(listener)でリスナを登録ている最中にノードが増減した場合に、メンバ変更のイベントが正しく通知されない場合がある不具合を修正した。
  * join()を呼び出して、参加待ちの状態で、他ノードの増減によるメンバ変更通知を受け取ると、参加せずに主ノードとして振る舞ってしまう不具合を修正した。
  * join()を呼び出して、参加待ちの状態で、他ノードの増減によるメンバ変更通知を受け取ると、登録されているClusterListenerに、メンバ変更通知のイベントを通知しない場合がある不具合を修正した。
  * スプリットブレインが発生した場合に、正しくマージできない場合がある不具合を修正した。
  * スプリットブレインの解消時に、ClusterListener#memberChange()に、正しいメンバ情報が渡らない不具合を修正した。
  * 稼働系に成り代わる際に、他ノードから応答がないと、稼働系が存在しないままになる不具合を修正した。
  * スプリットブレイン発生時に、複数ノードのクラスタが、稼働系疑いの状態に陥り、メンバ統合が、行われなくなる場合がある不具合を修正した。
37. jp.ossc.nimbus.service.message.MessageRecordFactoryServiceの不具合を修正
  * メッセージの上書きができない不具合を修正した。
  * restart()すると、デフォルトのメッセージ定義が重複してロードされる不具合を修正した。
38. jp.ossc.nimbus.service.proxy.RemoteServiceServerServiceの不具合を修正
  * Externalizerを設定している状態で、クライアント側でリトライ実行すると、引数の直列化が重ねて行われてしまう不具合を改修した。
39. jp.ossc.nimbus.service.publish.ClusterClientConnectionImplの不具合を修正
  * サービスの停止時にデッドロックする可能性がある不具合を修正した。
40. jp.ossc.nimbus.service.publish.ClusterConnectionFactoryServiceの不具合を修正
  * DGCで、Remoteオブジェクトがガベージされて、クライアント側から接続できなくなる不具合を修正した。
41. jp.ossc.nimbus.service.publish.DefaultPublisherServiceの不具合を修正
  * ソケットチャネルの終了時に、finishConnect()を呼び出していた不具合を修正した。
42. jp.ossc.nimbus.service.publish.DistributedClientConnectionImplの不具合を修正
  * close()しても、isConnected()がfalseにならない不具合を修正した。
  * close()実行時に、60秒かかる場合がある不具合を修正した。
43. jp.ossc.nimbus.service.publish.DistributedServerConnectionImplの不具合を修正
  * getClientCount()が、分散した数の倍数になってしまう不具合を修正した。
44. jp.ossc.nimbus.service.publish.GroupClientConnectionImplの不具合を修正
  * stopReceive()して、addSubject()すると、内部的にstartReceive()が実行される不具合を修正した。
  * stopReceive()を呼び出すと、startReceive()しても、受信できない不具合を修正した。
45. jp.ossc.nimbus.service.publish.GroupServerConnectionImplの不具合を修正
  * send()、sendAsynch()を呼び出した時に、複数の送信先コネクションが該当する場合、そのうちの１つにしか送信しない不具合を修正した。
  * send()、sendAsynch()を呼び出した時に、複数の送信先コネクションが該当する場合で、各コネクションの送信プロトコルが異なると、メッセージ送信に失敗する不具合を修正した。
46. jp.ossc.nimbus.service.publish.MessageForwardingServiceの不具合を修正
  * getReceiveCount()が、カウントアップされない不具合を修正した。
  * サービスの停止時にNullPointerExceptionが発生する場合がある不具合を修正した。
47. jp.ossc.nimbus.service.publish.MessageReceiverServiceの不具合を修正
  * stopReceive()を呼び出すと、startReceive()しても、受信できない不具合を修正した。
  * サービスの停止時に、60秒かかる場合がある不具合を修正した。
48. jp.ossc.nimbus.service.publish.RemoteClientConnectionFactoryの不具合を修正
  * DGCで、Remoteオブジェクトがガベージされて、クライアント側から接続できなくなる不具合を修正した。
49. jp.ossc.nimbus.service.publish.RequestConnectionFactoryServiceの不具合を修正
  * RequestTimeoutException発生時に、ConcurrentModificationExceptionが発生する場合がある不具合を修正した。
  * sendAsynch(Message)を呼んでも、非同期送信ではなく、同期送信になってしまう不具合を修正した。
50. jp.ossc.nimbus.service.publish.tcp.ClientConnectionImplの不具合を修正
  * close()した後に、送信処理を行うメソッドを呼び出すと、NullPointerExceptionが発生する不具合を修正した。
51. jp.ossc.nimbus.service.publish.tcp.ServerConnectionImplの不具合を修正
  * ソケットチャネルの終了時に、finishConnect()を呼び出していた不具合を修正した。
  * 再送要求処理時に、メッセージがロストする場合がある不具合を修正した。
  * close()実行時に、60秒かかる場合がある不具合を修正した。
  * getClientIds()実行時に、ConcurrentModificationExceptionが発生する場合がある不具合を修正した。
52. jp.ossc.nimbus.service.publish.udp.ClientConnectionImplの不具合を修正
  * マルチキャスト配信の場合に、登録していないサブジェクトのMessageを受信してしまう不具合を修正した。
  * マルチキャスト配信でロストが発生した場合に、ClassCastExceptionが発生する不具合を修正した。
  * 初回Messageが重複して、届いた場合に正しく受信できない場合がある不具合を修正した。
  * Windowの再利用処理で、ArrayIndexOutOfBoundsExceptionが発生する不具合を修正した。
  * 受信するサブジェクトの登録をしていない状態で、電文を受信するとNullPointerExceptionが発生する不具合を修正した。
  * 最初のメッセージがロストした際に、補完処理が正しく動作しない不具合を修正した。
  * 初メッセージを正しく受信できていない場合の補完処理で、不要なロストエラーログが出力される不具合を修正した。
53. jp.ossc.nimbus.service.publish.udp.MulticastMessageImplの不具合を修正
  * メッセージの再利用後にcontainsId(id)が正しく動作しなくなる不具合を修正した。
54. jp.ossc.nimbus.service.publish.udp.ServerConnectionImplの不具合を修正
  * ソケットチャネルの終了時に、finishConnect()を呼び出していた不具合を修正した。
  * UDPユニキャストでsubjectによる受信メッセージの選択を行うと、メッセージの受信漏れが起きる不具合を修正した。
  * 最初のメッセージがロストした場合に、正しく補完できない不具合を修正した。
  * 補完リクエストを受けた時に、ArrayIndexOutOfBoundsExceptionが発生する場合がある不具合を修正した。
  * 初回Messageが重複して、届いた場合に正しく受信できない場合がある不具合を修正した。
  * 送信キャッシュが２重に残る不具合を修正した。
  * 宛先付きのメッセージが送信キャッシュにキャッシュされてしまう不具合を修正した。
  * close()実行時に、60秒かかる場合がある不具合を修正した。
  * クライアントがclose()された時に、出力するログを、間違ってサーバがclose()する時に出力していたのを修正した。
  * ACKモードで、サブジェクトの削除を要求された時に、既に削除されていた場合、ACKを返さない不具合を修正した。
  * close()時に、NullPointerExceptionが発生する場合がある不具合を修正した。
55. jp.ossc.nimbus.service.publish.websocket.AbstractJMSMessageDispatcherServiceの不具合を修正
  * getMessageReceiveCount()がカウントアップされない不具合を修正した。
56. jp.ossc.nimbus.service.queue.DefaultQueueServiceの不具合を修正
  * createdの状態から、destroyすると、NullPointerExceptionが発生する不具合を修正した。
57. jp.ossc.nimbus.service.queue.QueueHandlerContainerServiceの不具合を修正
  * queueHandlerSizeが0で、Queueサービスが設定されているとき、push()しても、処理されない不具合を修正した。
58. jp.ossc.nimbus.service.queue.SharedQueueServiceの不具合を修正
  * キューの要素が取得されているのに、残っているようにみえる場合がある不具合を修正した。
  * maxThresholdSizeを指定すると、その件数に到達するとpushできなくなる不具合を修正した。
59. jp.ossc.nimbus.service.rest.BeanFlowRestServerServiceの不具合を修正
  * setContextServiceName(ServiceName)で設定されたContextサービスが使用されない不具合を修正した。
60. jp.ossc.nimbus.service.scheduler2.DatabaseScheduleManagerServiceの不具合を修正
  * スケジュール作成時に、必要のないデータをSELECTする不具合を修正した。
  * 同一時間で、相互依存しているスケジュールが存在すると、ジョブ実行がデッドロックになる不具合を修正した。
  * MySQLを使ってfindExecutableSchedules()を呼び出すと、SQLExceptionが発生する不具合を修正した。
61. jp.ossc.nimbus.service.semaphore.MemorySemaphoreの不具合を修正
  * getMaxWaitedCount()が実際の数より１小さくなる不具合を修正した。
62. jp.ossc.nimbus.service.websocket.SessionPropertiesの不具合を修正
  * getSessionProperty()で、NullPointerExceptionが発生する場合がある不具合を修正した。
63. jp.ossc.nimbus.service.writer.OneWriteFileMessageWriterServiceの不具合を修正
  * setEveryTimeCloseStream(true)で、setAppend(false)にすると無限ループになる不具合を修正した。
  * サービスの停止後に、ファイル出力をしようとすると、エラーが発生する場合がある不具合を修正した。
64. jp.ossc.nimbus.servlet.BeanFlowServletの不具合を修正
  * 初期化パラメータValidateをfalseに指定しても、検証BeanFlowを実行しようとする不具合を修正した。
65. jp.ossc.nimbus.servlet.ScheduleManagerServletの不具合を修正
  * サーブレットを複数階層のパスに配置すると、HTMLに出力されるURLが正しくなくなる不具合を修正した。
66. jp.ossc.nimbus.servlet.ServiceManagerFactoryServletの不具合を修正
  * サーブレットを複数階層のパスに配置すると、HTMLに出力されるURLが正しくなくなる不具合を修正した。
67. jp.ossc.nimbus.servlet.SharedContextServletの不具合を修正
  * サーブレットを複数階層のパスに配置すると、HTMLに出力されるURLが正しくなくなる不具合を修正した。
68. jp.ossc.nimbus.util.converter.BeanJSONConverterの不具合を修正
  * 不要な空白文字の読み飛ばし処理が不足しており、正しくパースできない場合がある不具合を修正した。
69. jp.ossc.nimbus.util.converter.DataSetJSONConverterの不具合を修正
  * Recordのプロパティ値はnullでないが、出力変換結果がnullになる場合に、setOutputNullProperty(true)に設定していても、出力されてしまう不具合を修正した。
70. jp.ossc.nimbus.util.converter.DecimalFormatConverterの不具合を修正
  * NaN、Infiniteの場合、NA値だと判定できない不具合を修正した。


### 機能変更

1. jp.ossc.nimbus.beans.NimbusPropertyEditorManagerの変更
  * java.sq.Date、java.sql.Time、java.io.File配列のPropertyEditorを登録した。
2. jp.ossc.nimbus.beans.SimplePropertyの変更
  * Recordのスキーマに存在しないプロパティを取得しようとすると、InvocationTargetExceptionが発生するのを、NoSuchPropertyExceptionを発生させるように変更した。
3. jp.ossc.nimbus.beans.dataset.DataSetCodeGeneratorの変更
  * XMLのパースエラーの場合に、行番号、列番号を表示するように変更した。
4. jp.ossc.nimbus.beans.dataset.DefaultPropertySchemaの変更
  * 型チェック時に、PropertySetExceptionが発生した場合に、発生した例外をthrowするPropertySchemaCheckExceptionのcauseに設定するように変更した。
5. jp.ossc.nimbus.beans.dataset.Recordの変更
  * 以下のメソッドのアクセス修飾子を変更した。
  * protected RecordList getRecordList()→public RecordList getRecordList()
6. jp.ossc.nimbus.core.AttributeMetaDataの変更
  * attribute要素にnullValue属性を追加した。
  * field要素にnullValue属性を追加した。
7. jp.ossc.nimbus.core.DefaultServiceLoaderServiceの変更
  * シャットダウンフックのスレッドに名前を付けた。
  * XMLのパースエラーの場合に、行番号、列番号を表示するように変更した。
8. jp.ossc.nimbus.core.DeploymentExceptionの変更
  * 以下のメソッドを追加した。
     * public void setResourceName(String)
     * public String getResourceName()
9. jp.ossc.nimbus.core.FieldMetaDataの変更
  * attribute要素にnullValue属性を追加した。
  * field要素にnullValue属性を追加した。
10. jp.ossc.nimbus.core.LoggerWrapperの変更
  * 以下のメソッドを追加した。
     * public synchronized void start()
     * public synchronized void stop()
11. jp.ossc.nimbus.core.MetaDataの変更
  * XML要素の内容の文字列を取得する際に、後ろだけトリムするように変更した。
12. jp.ossc.nimbus.core.ServiceManagerFactoryの変更
  * 全てのサービスをアンロードすると、デフォルトLoggerのデーモンスレッドを停止するように変更した。
13. jp.ossc.nimbus.core.ServiceNameの変更
  * 引数なしのコンストラクタを追加した。
14. jp.ossc.nimbus.io.RecursiveSearchFileの変更
  * 以下のメソッドを追加した。
     * public boolean deleteAllTree()
     * public boolean deleteAllTree(boolean)
     * public static boolean deleteAllTree(File)
     * public static boolean deleteAllTree(File, boolean)
     * public copyAllTree(File)
     * public boolean copyAllTree(File, FilenameFilter)
     * public static boolean copyAllTree(File, File)
     * public static boolean copyAllTree(File, File, FilenameFilter)
     * public static void dataCopy(File, File)
     * public void deleteOnExitAllTree()
     * public static void deleteOnExitAllTree(File)
15. jp.ossc.nimbus.service.aop.InterceptorChainの変更
  * 以下のメソッドを追加した。
     * public void setInvoker(Invoker)
16. jp.ossc.nimbus.service.aop.DefaultInterceptorChainの変更
  * cloneChain()で、currentIndexは複製しないように変更した。
  * 以下のメソッドを追加した。
     * public void setMetricsInfoMap(Map)
     * public Map getMetricsInfoMap()
     * public void setCalculateOnlyNormal(boolean)
     * public boolean isCalculateOnlyNormal()
17. jp.ossc.nimbus.service.aop.DefaultThreadLocalInterceptorChainの変更
  * cloneChain()で、currentIndexは複製しないように変更した。
18. jp.ossc.nimbus.service.aop.InterceptorChainFactoryの変更
  * 以下のメソッドを追加した。
     * public InterceptorChainList getInterceptorChainList(Object);
     * public Invoker getInvoker(Object)
19. jp.ossc.nimbus.service.aop.DefaultInterceptorChainFactoryServiceの変更
  * getInterceptorChain(Object)で、返すべきInterceptorChainもInvokerも存在しない場合には、nullを返すように変更した。
  * 以下のメソッドを追加した。
     * public InterceptorChainList getInterceptorChainList(Object)
     * public Invoker getInvoker(Object)
     * public String displayMetricsInfo()
     * public void reset()
     * public void setGetMetrics(boolean)
     * public boolean isGetMetrics()
     * public void setCalculateOnlyNormal(boolean)
     * public boolean isCalculateOnlyNormal()
     * public void setDateFormat(String)
     * public String getDateFormat()
     * public void setOutputTimestamp(boolean)
     * public boolean isOutputTimestamp()
     * public void setOutputCount(boolean)
     * public boolean isOutputCount()
     * public void setOutputExceptionCount(boolean)
     * public boolean isOutputExceptionCount()
     * public void setOutputErrorCount(boolean)
     * public boolean isOutputErrorCount()
     * public void setOutputLastTime(boolean)
     * public boolean isOutputLastTime()
     * public void setOutputLastExceptionTime(boolean)
     * public boolean isOutputLastExceptionTime()
     * public void setOutputLastErrorTime(boolean)
     * public boolean isOutputLastErrorTime()
     * public void setOutputBestPerformance(boolean)
     * public boolean isOutputBestPerformance()
     * public void setOutputBestPerformanceTime(boolean)
     * public boolean isOutputBestPerformanceTime()
     * public void setOutputWorstPerformance(boolean)
     * public boolean isOutputWorstPerformance()
     * public void setOutputWorstPerformanceTime(boolean)
     * public boolean isOutputWorstPerformanceTime()
     * public void setOutputAveragePerformance(boolean)
     * public boolean isOutputAveragePerformance()
20. jp.ossc.nimbus.service.aop.SelectableServletFilterInterceptorChainListServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
21. jp.ossc.nimbus.service.aop.interceptor.BeanFlowMetricsInterceptorServiceの変更
  * 性能対策として、java.util.concurrentを利用するように変更した。
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getPerformanceRecorderServiceName()
22. jp.ossc.nimbus.service.aop.interceptor.MethodJournalInterceptorServiceの変更
  * 以下のメソッドを追加した。
     * public void setContextJournalMapping(String, String)
     * public String getContextJournalMapping(String)
     * public Map getContextJournalMap()
     * public void setInvocationContextJournalMapping(String, String)
     * public String getInvocationContextJournalMapping(String)
     * public Map getInvocationContextJournalMap()
23. jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorServiceの変更
  * 性能対策として、java.util.concurrentを利用するようにした。
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getPerformanceRecorderServiceName()
24. jp.ossc.nimbus.service.aop.interceptor.RequestProcessCheckInterceptorServiceの変更
  * 性能対策として、java.util.concurrentを利用するように変更した。
  * Threshold属性で、ログの出力間隔も指定できるように変更した。
25. jp.ossc.nimbus.service.aop.interceptor.servlet.AccessJournalInterceptorServiceの変更
  * Sequenceサービスから発行した通番を、Contextサービスに設定するようにした。
26. jp.ossc.nimbus.service.aop.interceptor.servlet.AuthenticateInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
27. jp.ossc.nimbus.service.aop.interceptor.servlet.BlockadeInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
  * BlockadeExceptionをthrowする場合に、閉塞コードマスタのメッセージが指定されている場合は、利用するように変更した。
  * 閉塞状態を全閉塞と部分閉塞に分けるように変更した。
  * 以下のメソッドを追加した。
     * public void setBlockadeMapping(Map)
     * public Map getBlockadeMapping()
28. jp.ossc.nimbus.service.aop.interceptor.servlet.DatabaseAuthenticateStoreServiceの変更
  * 以下のメソッドを追加した。
     * public void setDeleteQueryOnCreate(String)
     * public String getDeleteQueryOnCreate()
     * public void setDeleteFindUser(boolean)
     * public boolean isDeleteFindUser()
29. jp.ossc.nimbus.service.aop.interceptor.servlet.DefaultExceptionHandlerServiceの変更
  * 以下のメソッドを追加した。
     * public void setRedirectPath(String)
     * public String getRedirectPath()
30. jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletRequestMetricsInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
  * 性能対策として、java.util.concurrentを利用するようにした。
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getPerformanceRecorderServiceName()
31. jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletResponseDeflateInterceptorServiceの変更
  * 以下のメソッドを追加した。
     * public long getResponseCount()
     * public long getCompressCount()
     * public long getCompressedCount()
     * public double getCompressRate()
     * public double getCompressedRate()
     * public double getAverageCompressionRate()
     * public void setPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getPerformanceRecorderServiceName()
     * public void setBeforeCompressSizePerformanceRecorderServiceName(ServiceName)
     * public ServiceName getBeforeCompressSizePerformanceRecorderServiceName()
     * public void setAfterCompressSizePerformanceRecorderServiceName(ServiceName)
     * public ServiceName getAfterCompressSizePerformanceRecorderServiceName()
32. jp.ossc.nimbus.service.aop.interceptor.servlet.SelectableServletFilterInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
33. jp.ossc.nimbus.service.aop.interceptor.servlet.ServletFilterInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
34. jp.ossc.nimbus.service.aop.interceptor.servlet.StreamExchangeInterceptorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
  * リクエストとレスポンスのどちらか片方のStreamConverterのみでも、サービスが利用できるように変更した。
35. jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerServiceの変更
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
36. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccessの変更
  * 以下のメソッドを追加した。
     * public void setResourcePath(String)
  * 以下のメソッドのシグニチャを変更した。
     * public void fillInstance(Element, BeanFlowInvokerFactoryCallBack)
       → public void fillInstance(Element, BeanFlowInvokerFactoryCallBack,String)
37. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccessImpl2の変更
  * maxThreads、timeout、maxWaitCount、forceFreeTimeout属性で、環境変数を参照できるようにした。
  * for要素のbegin属性とend属性に、${}による環境変数参照が記述できるようにした。
  * TemplateEngineサービスを利用するtemplate要素を追加した。
  * attribute要素にnullValue属性を追加した。
  * field要素にnullValue属性を追加した。
  * Interpreterが設定されていない場合でも、読み込み時にはエラーにしないように動作変更した。
  * reply要素の下に、catch要素、finally要素を書けるようにした。
  * reply要素のtimeout属性に式を書けるようにした。
  * 性能対策として、非同期呼び出し時に、応答待ちしない場合は、呼び出し先のBeanFlowMonitorを管理しないようにした。
  * for要素、while要素にjournalOnlyLast属性を追加した。
  * 以下のメソッドを追加した。
     * public BeanFlowCoverage getBeanFlowCoverage()
     * public String getResourcePath()
     * public void setResourcePath(String)
38. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerFactoryCallBackの変更
  * 以下のメソッドのシグニチャを変更した。
     * public Journal getJournal()→public Journal getJournal(BeanFlowInvokerAccess)
  * 以下のメソッドを追加した。
     * public Interpreter getTestInterpreter()
     * public TemplateEngine getTemplateEngine()
39. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServerの変更
  * 以下のメソッドを追加した。
     * public BeanFlowCoverage getBeanFlowCoverage(Object)
     * public String getResourcePath(Object)
40. jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServerServiceの変更
  * jndiRepositoryServiceName属性を必須から、オプションに変更した。
  * 以下のメソッドを追加した。
     * public void setRMIClientSocketFactoryServiceName(ServiceName)
     * public ServiceName getRMIClientSocketFactoryServiceName()
     * public void setRMIServerSocketFactoryServiceName(ServiceName)
     * public ServiceName getRMIServerSocketFactoryServiceName()
     * public BeanFlowCoverage getBeanFlowCoverage(Object)
     * public String getResourcePath(Object)
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
     * public void setClusterJoin(boolean)
     * public boolean isClusterJoin()
     * public Object getHostInfo()
41. jp.ossc.nimbus.service.beancontrol.ClientBeanFlowInvokerFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public BeanFlowCoverage getBeanFlowCoverage()
     * public String getResourcePath()
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
42. jp.ossc.nimbus.service.beancontrol.DefaultBeanFlowInvokerFactoryServiceの変更
  * 業務フロー定義ファイルをサービス定義ファイルからの相対パスで指定できるように変更した。
  * 業務フロー定義ファイルのディレクトリを指定した場合に、指定されたディレクトリ配下を再帰的に見て、業務フロー定義ファイルを読み込むように変更した。
  * AsynchInvokeQueueHandlerContainerServiceName属性で指定したQueueHandlerContainerにQueueHandlerがあらかじめ指定してある場合は、そのまま使用するように変更した。
  * XMLのパースエラーの場合に、行番号、列番号を表示するように変更した。
  * 以下のメソッドを追加した。
     * public void setTemplateEngineServiceName(ServiceName)
     * public ServiceName getTemplateEngineServiceName()
     * public TemplateEngine getTemplateEngine()
     * public void setTestInterpreterServiceName(ServiceName)
     * public ServiceName getTestInterpreterServiceName()
     * public void setTestInterpreterServiceName(ServiceName)
     * public ServiceName getTestInterpreterServiceName()
     * public void setJournalPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getJournalPerformanceRecorderServiceName()
     * public void setCollectJournalMetrics(boolean)
     * public boolean isCollectJournalMetrics()
     * public void setOutputJournalMetricsCount(boolean)
     * public boolean isOutputJournalMetricsCount()
     * public void setOutputJournalMetricsLastTime(boolean)
     * public boolean isOutputJournalMetricsLastTime()
     * public void setOutputJournalMetricsBestSize(boolean)
     * public boolean isOutputJournalMetricsBestSize()
     * public void setOutputJournalMetricsBestSizeTime(boolean)
     * public boolean isOutputJournalMetricsBestSizeTime()
     * public void setOutputJournalMetricsWorstSize(boolean)
     * public boolean isOutputJournalMetricsWorstSize()
     * public void setOutputJournalMetricsWorstSizeTime(boolean)
     * public boolean isOutputJournalMetricsWorstSizeTime()
     * public void setOutputJournalMetricsAverageSize(boolean)
     * public boolean isOutputJournalMetricsAverageSize()
     * public void setOutputJournalMetricsTimestamp(boolean)
     * public boolean isOutputJournalMetricsTimestamp()
     * public void resetJournalMetrics()
     * public String displayJournalMetricsInfo()
43. jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerの変更
  * 以下のメソッドを追加した。
     * public BeanFlowCoverage getBeanFlowCoverage()
     * public String getResourcePath()
44. jp.ossc.nimbus.service.beancontrol.interfaces.InvalidConfigurationExceptionの変更
  * 以下のメソッドを追加した。
     * public void setResourceName(String)
     * public String getResourceName()
45. jp.ossc.nimbus.service.cache.ContextSaveOverflowActionServiceの変更
  * 永続化したキーが、キャッシュから削除された場合は、永続化先からも削除するようにした。
46. jp.ossc.nimbus.service.cache.FileCacheServiceの変更
  * load()時に、ファイルの更新時刻でソートして読み込むように変更した。
47. jp.ossc.nimbus.service.cache.SerializedMemoryCacheMapServiceの変更
  * PersistableCacheインタフェースを実装した。
  * 以下のメソッドを追加した。
     * public void setLoadOnStart(boolean)
     * public boolean isLoadOnStart()
     * public void setSaveOnStop(boolean)
     * public boolean isSaveOnStop()
48. jp.ossc.nimbus.service.cache.SerializedMemoryCacheServiceの変更
  * PersistableCacheインタフェースを実装した。
  * 以下のメソッドを追加した。
     * public void setLoadOnStart(boolean)
     * public boolean isLoadOnStart()
     * public void setSaveOnStop(boolean)
     * public boolean isSaveOnStop()
49. jp.ossc.nimbus.service.codemaster.CodeMasterFinderの変更
  * 以下のメソッドを追加した。
     * public void updateAllCodeMasters()
50. jp.ossc.nimbus.service.codemaster.CodeMasterFinderGroupServiceの変更
  * 以下のメソッドを追加した。
     * public void updateAllCodeMasters()
     * public String[] getNotUpdateAllMasterNames()
     * public void setNotUpdateAllMasterNames(String[])
51. jp.ossc.nimbus.service.codemaster.CodeMasterServiceの変更
  * save()メソッド呼び出し時に、マスタの永続化に失敗した場合、エラーログを出力するようにした。
  * 以下のメソッドを追加した。
     * public void updateAllCodeMasters()
     * public String[] getNotUpdateAllMasterNames()
     * public void setNotUpdateAllMasterNames(String[])
52. jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceの変更
  * 以下のメソッドを追加した。
     * public void updateAllCodeMasters()
     * public String[] getNotUpdateAllMasterNames()
     * public void setNotUpdateAllMasterNames(String[])
53. jp.ossc.nimbus.service.connection.DefaultPersistentManagerServiceの変更
  * load時に、列の型がTIMEの時は、getTime()で取得するように変更した。
  * load時に、列の型がTIMESTAMPの時は、getTimestamp()で取得するように変更した。
  * クエリで"->{プロパティ文字列}"、"<-{プロパティ文字列}"を指定する時に、プロパティ文字列内に}が出てきてもパースできるように、プロパティ文字列を"または'でエスケープできるようにした。
54. jp.ossc.nimbus.service.connection.TableCreatorServiceの変更
  * setInsertRecordsFilePath(String)で指定するパスに、サービス定義ファイルからの相対パス指定が可能になるようにした。
55. jp.ossc.nimbus.service.connection.TransactionSynchronizerServiceの変更
  * 以下のメソッドを追加した。
     * public void setSynchronizeColumnName(String)
     * public String getSynchronizeColumnName()
     * public void setDeleteOnSynchronize(boolean)
     * public boolean isDeleteOnSynchronize()
     * public void setGarbageSynchronizeColumnNames(String[])
     * public String[] getGarbageSynchronizeColumnNames()
     * public void setGarbageTime(long)
     * public long getGarbageTime()
     * public void setUpdateUser(String)
     * public String getUpdateUser()
56. jp.ossc.nimbus.service.context.DefaultContextServiceの変更
  * 以下のメソッドを追加した。
     * public Object put(String, String)
57. jp.ossc.nimbus.service.context.SharedContextの変更
  * 以下のメソッドを追加した。
     * public int getLockWaitCount(Object)
     * public Object get(Object, long, boolean)
     * public boolean lock(Object, boolean, boolean, long)
     * public void analyzeIndex(String, long)
58. jp.ossc.nimbus.service.context.SharedContextServiceの変更
  * lock()メソッドの呼び出し順序通りに、ロック待ちを解除するように変更した。
  * クライアントモードで、インデックスが設定されていない場合に、analyzeIndex(String)を呼び出すと、サーバモードのノードのanalyzeIndex(String)を呼び出すように変更した。
  * 以下のメソッドを追加した。
     * public void setInterpretContextVariableName(String)
     * public String getInterpretContextVariableName()
     * public int getActiveExecuteThreadSize()
     * public int getMaxActiveExecuteThreadSize()
     * public int getLockWaitCount(Object)
     * public float getCacheHitRatio()
     * public void resetCacheHitRatio()
     * public void setWaitConnectAllOnStart(boolean)
     * public boolean isWaitConnectAllOnStart()
     * public void setWaitConnectTimeout(long)
     * public long getWaitConnectTimeout()
     * public Object get(Object, long, boolean)
     * public boolean lock(Object, boolean, boolean, long)
     * public void analyzeIndex(String, long)
  * 以下のメソッドを削除した。
     * public int getActiveExecuteThreadSize()
     * public int getMaxActiveExecuteThreadSize()
59. jp.ossc.nimbus.service.context.DistributedSharedContextの変更
  * 以下のメソッドを追加した。
     * public int getMainNodeCount()
60. jp.ossc.nimbus.service.context.DistributedSharedContextServiceの変更
  * クライアントモードの場合は、サービスの開始時にrehash要求を行わないように変更した。
  * ノードが減った時に、無条件にrehashを行っていたのを、サーバノードが減った時のみrehashを行うように変更した。
  * クライアントモードで、インデックスが設定されていない場合に、analyzeIndex(String)を呼び出すと、サーバモードのノードのanalyzeIndex(String)を呼び出すようにした。
  * 以下のメソッドを追加した。
     * public void setInterpretContextVariableName(String)
     * public String getInterpretContextVariableName()
     * public int getLockWaitCount(Object)
     * public float getCacheHitRatio()
     * public void resetCacheHitRatio()
     * public int getMainNodeCount()
     * public void setWaitConnectAllOnStart(boolean)
     * public boolean isWaitConnectAllOnStart()
     * public void setWaitConnectTimeout(long)
     * public long getWaitConnectTimeout()
     * public Object get(Object, long, boolean)
     * public boolean lock(Object, boolean, boolean, long)
     * public void analyzeIndex(String, long)
61. jp.ossc.nimbus.service.context.SharedContextValueDifferenceSupportの変更
  * 以下のメソッドのシグニチャを変更した。
     * public boolean update(SharedContextValueDifference)→public int update(SharedContextValueDifference)
62. jp.ossc.nimbus.service.context.SharedContextRecordの変更
  * 以下のメソッドのシグニチャを変更した。
     * public boolean update(SharedContextValueDifference)→public int update(SharedContextValueDifference)
63. jp.ossc.nimbus.service.context.SharedContextRecordListの変更
  * 以下のメソッドのシグニチャを変更した。
     * public boolean update(SharedContextValueDifference)→public int update(SharedContextValueDifference)
64. jp.ossc.nimbus.service.context.SharedContextTransactionManagerの変更
  * 以下のメソッドのシグニチャを変更した。
     * public Object get(SharedContext, Object)→public Object get(SharedContext, Object, long)
65. jp.ossc.nimbus.service.context.SharedContextTransactionManagerServiceの変更
  * 以下のメソッドのシグニチャを変更した。
     * public Object get(SharedContext, Object)→public Object get(SharedContext, Object, long)
66. jp.ossc.nimbus.service.context.SharedContextIndexの変更
  * List形式の値に対するインデックスをサポートした。
67. jp.ossc.nimbus.service.context.SharedContextIndexManagerの変更
  * 以下のメソッドを追加した。
     * public boolean hasIndex(String)
68. jp.ossc.nimbus.service.ejb.EJBFactoryの変更
69. jp.ossc.nimbus.service.ejb.GroupEJBFactoryServiceの変更
70. jp.ossc.nimbus.service.ejb.InvocationEJBFactoryServiceの変更
71. jp.ossc.nimbus.service.ejb.UnitEJBFactoryServiceの変更
72. jp.ossc.nimbus.service.ejb.UnitEJBFactoryFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public EJBLocalObject getLocal(String)
     * public EJBLocalObject getLocal(String, Object[])
     * public EJBLocalObject getLocal(String, Class)
     * public EJBLocalObject getLocal(String, Class, Class[], Object[])
     * public EJBLocalObject getLocal(String, Class, Class, Class[], Object[])
73. jp.ossc.nimbus.service.ftp.ftpclient.FTPClientFactoryServiceの変更
  * setHomeDirectory()が設定されている場合で、ディレクトリが存在しない場合は、サービスの起動時に生成するように変更した。
  * 以下のメソッドを追加した。
     * public void setConnectMaxRetryCount(int)
     * public int getConnectMaxRetryCount()
  * 以下のメソッドのシグニチャを変更した。
     * public void removeTPClientProperty(String)→public void removeFTPClientProperty(String)
74. jp.ossc.nimbus.service.graph.JFreeChartFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public void setPlotFactory(PlotFactory)
75. jp.ossc.nimbus.service.graph.XYPlotFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public void addDatasetFactory(DatasetFactory)
76. jp.ossc.nimbus.service.http.HttpRequestの変更
77. jp.ossc.nimbus.service.http.httpclient.HttpRequestImplの変更
  * 以下のメソッドを追加した。
     * public void setFileParameter(String, File)
     * public void setFileParameter(String, String, File)
  * 以下のメソッドのシグニチャを変更した。
     * public void setFileParameter(String, String, File)→public void setFileParameter(String, File, String, String)
78. jp.ossc.nimbus.service.http.httpclient.HttpClientFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getPerformanceRecorderServiceName()
  * 以下のメソッドを追加した。
     * public void setFileParameter(String, File)
     * public void setFileParameter(String, String, File)
79. jp.ossc.nimbus.service.http.proxy.Processの変更
  * 以下のメソッドのシグニチャを変更した。
     * public void doProcess(InputStream, OutputStream)→public void doProcess(Socket)
80. jp.ossc.nimbus.service.http.proxy.HttpProcessServiceBaseの変更
  * CONNECTメソッドを使ったHTTPトンネリングを通過させるようにした。
  * 以下のメソッドを追加した。
     * public void setTunnelSocketFactoryServiceName(ServiceName)
     * public ServiceName getTunnelSocketFactoryServiceName()
     * public void setTunnelBufferSize(int)
     * public int getTunnelBufferSize()
     * public String getProxyHost()
     * public void setProxyHost(String)
     * public int getProxyPort()
     * public void setProxyPort(int)
     * public String getProxyUser()
     * public void setProxyUser(String)
     * public String getProxyPassword()
     * public void setProxyPassword(String)
  * 以下のメソッドのシグニチャを変更した。
     * public void doProcess(InputStream, OutputStream)→public void doProcess(Socket)
81. jp.ossc.nimbus.service.http.proxy.TestHttpProcessServiceの変更
  * 内部クラスActionに、以下のメソッドを追加した。
     * public void setInterpreterServiceName(ServiceName)
     * public void setInterpreter(Interpreter)
     * public void setResponseBodyEditScript(String)
82. jp.ossc.nimbus.service.http.proxy.HttpRequestの変更
  * POST、GET、DELETE、PUT以外のメソッドもサポートするようにした。
  * リクエストヘッダのTransfer-Encoding: chunkedに対応した。
  * 内部クラスRequestHeaderに、以下のメソッドを追加した。
     * public Map getHeaderMap()
  * 内部クラスRequestHeaderの、以下のメソッドのシグニチャを変更した。
     * protected String readLine(InputStream, ByteArrayOutputStream)→public static String readLine(InputStream, ByteArrayOutputStream)
83. jp.ossc.nimbus.service.http.proxy.HttpResponseの変更
  * ヘッダのコンテント長と実際のコンテント長が等しくない場合のみ、コンテントエンコーディングに指定された圧縮方式で圧縮を行うように変更した。
  * レスポンスヘッダに強制的に"Connection: close"を付与して、常時接続しないようにした。
  * 以下のメソッドを追加した。
     * public void setDateHeader(String, Date)
     * public String getTransferEncoding()
     * public String getConnection()
84. jp.ossc.nimbus.service.ioccall.DefaultFacadeCallServiceの変更
  * 以下のメソッドを追加した。
     * public void setLocal(boolean)
     * public boolean isLocal()
85. jp.ossc.nimbus.service.jmx.MBeanWatcherServiceの変更
  * プラットフォームMBeanServerも監視できるようにした。（JDK1.5以降）
  * 常時接続の場合（isConnectOnStart()=true）に、リスナを登録し、接続切断検知した時に、監視デーモンを一時停止するように変更した。
  * 接続エラーが発生した場合、次に接続が成功して、再度接続エラーが発生するまで、接続エラーのログを出力しないようにした。
  * 内部クラスConditionに、以下のメソッドを追加した。
     * public void setOnceOutputLog(boolean)
     * public void setInterpreterServiceName(ServiceName)
     * public void setInterpreter(Interpreter)
  * 内部クラスDivideOperationに、以下のメソッドを追加した。
     * public void setReturnZeroOnZeroDivide(boolean)
     * public boolean isReturnZeroOnZeroDivide()
  * 内部クラスMedian、Averageに以下のメソッドを追加した。
     * public void setScale(int)
     * public int getScale()
  * 以下の内部クラスを追加した。
     * MBeanWatcher
     * Interpreter
     * ChangeSet
     * Variance
     * StandardDeviation
     * PercentageOperation
     * Attributes
  * 内部クラスChangeで、集合に対する差分も取得できるようにした。
  * ContextKey属性の指定で、プロパティ表現を可能にした。
  * EditTargetでsetElementEdit(true)にした場合、配列を返していたのをListを返すように変更した。
  * Periodでも、PlatformMBeanServerを利用できるように変更した。
  * 以下のメソッドを追加した。
     * public void setResetOnStart(boolean)
     * public boolean isResetOnStart()
86. jp.ossc.nimbus.service.jndi.CachedJndiFinderServiceの変更
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
87. jp.ossc.nimbus.service.journal.Journalの変更
  * 以下のメソッドを追加した。
     * public void removeInfo(int)
88. jp.ossc.nimbus.service.journal.ThreadManagedJournalServiceの変更
  * getCurrentJournalString(null)で呼び出された場合、サービスに設定されているEditorFinderを採用するように変更した。
  * 以下のメソッドを追加した。
     * public void removeInfo(int)
89. jp.ossc.nimbus.service.journal.RequestJournalの変更
90. jp.ossc.nimbus.service.journal.RequestJournalImplの変更
  * 以下のメソッドを追加した。
     * public void clearParam(int)
91. jp.ossc.nimbus.service.journal.editor.BeanJournalEditorServiceの変更
  * isFieldOnly(Class)、isAccessorOnly(Class)で、引数で指定された型がキャスト可能な型に対しても、設定が有効になるようにした。
92. jp.ossc.nimbus.service.keepalive.AbstractKeepAliveCheckerSelectorServiceの変更
  * Clusterサービスで、選択可能なKeepAliveCheckerを選択する場合、KeepAliveChecker#isAlive()を評価するように変更した。
  * 以下のメソッドを追加した。
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
93. jp.ossc.nimbus.service.keepalive.ClusterServiceの変更
  * setUnicastMemberAddresses(String[])でポート番号の指定もできるように変更した。
  * Clientではなくjoin()していない場合に、getMembers()に自分自身を含めるように変更した。
  * Clientではなくjoin()した時に、mainでなければ、changeSub()を呼び出すように変更した。
  * 参加処理中の場合は、メンバ変更通知を受け取って、自分がメンバに含まれていない場合でも、参加要求を出さないように変更した。
  * マルチキャストで１対１通信を行う場合は、UDPユニキャストで通信するように変更した。
  * クラスタメンバが変更される時に、ログを出力するようにした。
  * メンバ追加の際に、アドレスおよびポートが同じ古いメンバ情報を削除するように変更した。
  * サーバモードが停止する時に、クライアントモードのメンバにも、終了通知を送るように変更した。
  * クライアントモードも、サーバモードのメンバの終了通知を受けて、メンバ変更を行うように変更した。
  * メインが成り代わった時に、メンバ変更通知を送るように変更した。
  * スプリットブレインが発生した場合に、参加しているメンバが多い方の主ノードを優先して、マージを行うように変更した。
  * ハートビートのリングの、前方からハートビートが来なくなった場合も、検知できるように変更した。
  * 以下のメソッドを追加した。
     * public void setOption(String, java.io.Serializable)
     * public java.io.Serializable getOption(String)
     * public void setAnonymousUnicastPort(boolean)
     * public boolean isAnonymousUnicastPort()
     * public void setSocketReceiveBufferSize(int)
     * public int getSocketReceiveBufferSize()
     * public void setSocketSendBufferSize(int)
     * public int getSocketSendBufferSize()
     * public boolean isMainDoubt()
     * public void setMainDoubt(boolean)
94. jp.ossc.nimbus.service.keepalive.KeepAliveCheckerの変更
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
95. jp.ossc.nimbus.service.keepalive.jdbc.JDBCKeepAliveCheckerServiceの変更
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
     * public void setHostResolverServiceName(ServiceName)
     * public ServiceName getHostResolverServiceName()
96. jp.ossc.nimbus.service.keepalive.smtp.SmtpCheckerServiceの変更
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
97. jp.ossc.nimbus.service.message.smtp.MessageRecordFactoryの変更
  * 以下のメソッドを追加した。
     * public String[] getMessageIds()
98. jp.ossc.nimbus.service.message.MessageRecordFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public String[] getMessageIds()
     * public void setLoadNimbusMessageFile(boolean)
     * public boolean isLoadNimbusMessageFile()
99. jp.ossc.nimbus.service.proxy.RemoteServiceServerServiceの変更
  * サーバ側の呼び出し対象のサービスがKeepAliveCheckerだった場合、死活監視時に、KeepAliveChecker#isAlive()を呼び出すように変更した。
  * RMI実行時に、予期しないエラーが発生した場合は、ログを出力するように変更した。
  * jndiRepositoryServiceName属性を必須から、オプションに変更した。
  * サーバを呼び出す時に、InvocationContextの属性"ClientAddress"を設定するように変更した。
  * ClusterServiceを使用する場合にも、Externalizerが使用できるように変更した。
  * 以下のメソッドを追加した。
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
     * public void setClusterJoin(boolean)
     * public boolean isClusterJoin()
     * public Object getHostInfo()
     * public void setExternalizerServiceName(ServiceName)
     * public ServiceName getExternalizerServiceName()
100. jp.ossc.nimbus.service.proxy.invoker.ClusterInvokerServiceの変更
  * 以下のメソッドを追加した。
     * public void setThreadContextServiceName(ServiceName)
     * public ServiceName getThreadContextServiceName()
101. jp.ossc.nimbus.service.proxy.invoker.LocalClientMethodCallInvokerServiceの変更
  * 呼び出し対象のローカルサービスの設定を、必須から任意に変更した。
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
102. jp.ossc.nimbus.service.proxy.invoker.RemoteClientMethodCallInvokerServiceの変更
  * サーバを呼び出す時に、InvocationContextの属性"ClientAddress"を設定するように変更した。
  * 以下のメソッドを追加した。
     * public Object getHostInfo()
     * public void setExternalizerServiceName(ServiceName)
     * public ServiceName getExternalizerServiceName()
103. jp.ossc.nimbus.service.publish.ClusterClientConnectionFactoryServiceの変更
  * getClientConnection()を呼び出すと、異なるClientConnectionを生成するように変更した。
  * 以下のメソッドを追加した。
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
104. jp.ossc.nimbus.service.publish.ClusterClientConnectionImplの変更
  * 管理しているクラスタを構成するコネクションを更新する際に、サーバから閉じられたコネクションを管理対象から除外するように変更した。
  * 以下のメソッドを追加した。
     * public void setClusterOptionKey(String)
     * public void setStartReceiveFromLastReceiveTime(boolean)
     * public boolean isStartReceiveFromLastReceiveTime()
105. jp.ossc.nimbus.service.publish.ClusterConnectionFactoryServiceの変更
  * jndiRepositoryServiceName属性を必須から、オプションに変更した。
  * 以下のメソッドを追加した。
     * public void setClusterOptionKey(String)
     * public String getClusterOptionKey()
     * public void setClusterJoin(boolean)
     * public boolean isClusterJoin()
     * public void setStartReceiveFromLastReceiveTime(boolean)
     * public boolean isStartReceiveFromLastReceiveTime()
106. jp.ossc.nimbus.service.publish.DefaultPublisherServiceの変更
  * ResourceUsageインタフェースを実装した。
  * 以下のメソッドを追加した。
     * public long getServantsSendMessageParamCreateCountAverage()
107. jp.ossc.nimbus.service.publish.DistributedClientConnectionImplの変更
  * 分散した接続の全てに対して処理が必要な場合、並列で処理できるようにした。
108. jp.ossc.nimbus.service.publish.DistributedServerConnectionImplの変更
  * 以下のメソッドを追加した。
     * public void reset()
109. jp.ossc.nimbus.service.publish.GroupServerConnectionImplの変更
  * 以下のメソッドを追加した。
     * public void reset()
110. jp.ossc.nimbus.service.publish.Messageの変更
  * 以下のメソッドを追加した。
     * public long getSendTime()
111. jp.ossc.nimbus.service.publish.MessageForwardingServiceの変更
  * ServerConnectionListenerのイベント処理を同期化した。
  * onConnect()の際に、接続されていない場合は、接続するようにした。
  * 以下のメソッドを追加した。
     * public void addSubject(String)
     * public void addSubject(String, String[])
     * public Set getSubjects()
     * public Set getKeys(String)
112. jp.ossc.nimbus.service.publish.MessageReceiverServiceの変更
  * MessageListenerへのパラメータをリサイクル出来るように変更した。
  * MessageListenerの登録順を保持するように変更した。
  * 以下のメソッドを追加した。
     * public void setMessageLatencyPerformanceRecorderServiceName(ServiceName)
     * public ServiceName getMessageLatencyPerformanceRecorderServiceName()
113. jp.ossc.nimbus.service.publish.RequestConnectionFactoryServiceの変更
  * RequestTimeoutException発生時のメッセージを詳細化した。
  * 以下のメソッドを追加した。
     * public int sendRequest(Message, int, long)
     * public int sendRequest(Message, String, String, int, long)
     * public Message[] getReply(int, long)
     * public void reset()
114. jp.ossc.nimbus.service.publish.RequestServerConnectionの変更
  * 以下のメソッドを追加した。
     * public int sendRequest(Message, int, long)
     * public int sendRequest(Message, String, String, int, long)
     * public Message[] getReply(int, long)
115. jp.ossc.nimbus.service.publish.Servantの変更
  * 以下のメソッドを追加した。
     * public long getSendMessageParamCreateCount()
116. jp.ossc.nimbus.service.publish.ServerConnectionの変更
  * 以下のメソッドを追加した。
     * public void reset()
117. jp.ossc.nimbus.service.publish.ThinOutServerConnectionImplの変更
  * 以下のメソッドを追加した。
     * public void reset()
118. jp.ossc.nimbus.service.publish.tcp.ClientConnectionImplの変更
  * 受信時に、SocketExceptionが発生した場合、例外をthrowする処理をclose()するように変更した。
119. jp.ossc.nimbus.service.publish.tcp.ConnectionFactoryServiceの変更
  * 再送要求を受信した時、そのクライアントが要求しているメッセージだけを再送するように変更した。
  * サーバ側の待ち受けソケットのポートを再利用可能に設定するように変更した。
  * 以下のメソッドのシグニチャを変更した。
     * public void setSendBufferTime(long)→public void setSendMessageCacheTime(long)
     * public long getSendBufferTime()→public long getSendMessageCacheTime()
  * 以下のメソッドを追加した。
     * public void setSendBufferTime(long)
     * public long getSendBufferTime()
     * public void setSendBufferSize(long)
     * public long getSendBufferSize()
     * public void setSendBufferTimeoutInterval(long)
     * public long getSendBufferTimeoutInterval()
     * public void setClientConnectMessageId(String)
     * public String getClientConnectMessageId()
     * public void setClientCloseMessageId(String)
     * public String getClientCloseMessageId()
     * public void setClientClosedMessageId(String)
     * public String getClientClosedMessageId()
120. jp.ossc.nimbus.service.publish.tcp.ServerConnectionImplの変更
  * 再送要求を受信した時、そのクライアントが要求しているメッセージだけを再送するように変更した。
  * クライアントがclose()された時、サブジェクトの削除や、受信停止を呼び出されていない場合は、内部処理として行うように変更した。
  * 内部エラーで、クライアントをclose()した場合に、原因をログに出力するように変更した。
  * 以下のコンストラクタのシグニチャを変更した。
     * public ServerConnectionImpl(ServerSocket, Externalizer, int, ServiceName, int, ServiceName, ServiceName)→public ServerConnectionImpl(ServerSocket, Externalizer, int, ServiceName, int, ServiceName, ServiceName, long, long, long)
     * public ServerConnectionImpl(ServerSocketChannel, Externalizer, int, ServiceName, int, ServiceName, ServiceName, SocketFactory)→public ServerConnectionImpl(ServerSocketChannel, Externalizer, int, ServiceName, int, ServiceName, ServiceName, SocketFactory, long, long, long)
  * 以下のメソッドのシグニチャを変更した。
     * public void setSendBufferTime(long)→public void setSendMessageCacheTime(long)
     * public int getSendBufferSize()→public int getSendMessageCacheSize()
  * 以下のメソッドを追加した。
     * public void setClientConnectMessageId(String)
     * public String getClientConnectMessageId()
     * public void setClientCloseMessageId(String)
     * public String getClientCloseMessageId()
     * public void reset()
     * public void setClientClosedMessageId(String)
     * public String getClientClosedMessageId()
121. jp.ossc.nimbus.service.publish.udp.ClientConnectionImplの変更
  * メッセージをまだ受信していない状態でも、ポーリングするようにした。
  * マルチキャスト配信でstartReceive(long)を呼び出した際に、遡って配信するメッセージは、最初の1件のみ配信するようにした。また、ユニキャスト配信の場合は、受信対象のメッセージのみに絞り込んで配信するようにした。
122. jp.ossc.nimbus.service.publish.udp.ConnectionFactoryServiceの変更
  * 再送要求を受信した時、そのクライアントが要求しているメッセージだけを再送するように変更した。
  * サーバ側の待ち受けソケットのポートを再利用可能に設定するように変更した。
  * 以下のメソッドのシグニチャを変更した。
     * public void setSendBufferTime(long)→public void setSendMessageCacheTime(long)
     * public long getSendBufferTime()→public long getSendMessageCacheTime()
     * public int getMostOldSendBufferSequence()→public int getMostOldSendMessageCacheSequence()
     * public Date getMostOldSendBufferTime()→public Date getMostOldSendMessageCacheTime()
     * public int getSendBufferSize()→public int getSendMessageCacheSize()
  * 以下のメソッドを追加した。
     * public double getMessageRecycleRate()
     * public double getWindowRecycleRate()
     * public void setSendBindAddress(String)
     * public String getSendBindAddress()
     * public void setClientConnectMessageId(String)
     * public String getClientConnectMessageId()
     * public void setClientCloseMessageId(String)
     * public String getClientCloseMessageId()
123. jp.ossc.nimbus.service.publish.udp.MessageIdの変更
  * 以下のメソッドを追加した。
     * public void copy(MessageImpl)
124. jp.ossc.nimbus.service.publish.udp.MessageImplの変更
  * 以下のメソッドのシグニチャを変更した。
     * public synchronized List getWindows(List, int, Externalizer)→public synchronized List getWindows(ServerConnectionImpl, int, Externalizer)
  * 以下のメソッドを追加した。
     * public void copy(MessageImpl)
125. jp.ossc.nimbus.service.publish.udp.MulticastMessageImplの変更
  * 以下のメソッドを追加した。
     * public void copy(MessageImpl)
126. jp.ossc.nimbus.service.publish.udp.Windowの変更
  * 以下のメソッドのシグニチャを変更した。
     * public static List toWindows(MessageImpl, List, int, Externalizer)→public static List toWindows(MessageImpl, ServerConnectionImpl, List, int, Externalizer)
  * 以下のメソッドを追加した。
     * public void setFirst(boolean)
     * public boolean isFirst()
127. jp.ossc.nimbus.service.publish.udp.ServerConnectionImplの変更
  * マルチキャストでの非同期送信では、並列配信を行わないように変更した。
  * メッセージをまだ受信していない状態でも、ポーリングするように変更した。
  * 再送要求を受信した時、そのクライアントが要求しているメッセージだけを再送するように変更した。
  * マルチキャスト配信でstartReceive(long)を呼び出した際に、遡って配信するメッセージは、最初の1件のみ配信するようにした。また、ユニキャスト配信の場合は、受信対象のメッセージのみに絞り込んで配信するようにした
  * クライアントがclose()された時、サブジェクトの削除や、受信停止を呼び出されていない場合は、内部処理として行うように変更した。
  * クライアントにメッセージを送信した時に、SocketExceptionが発生した場合は、無視するようにした。
  * 以下のメソッドのシグニチャを変更した。
     * public void setSendBufferTime(long)→public void setSendMessageCacheTime(long)
     * public int getSendBufferSize()→public int getSendMessageCacheSize()
     * public int getMostOldSendBufferSequence()→public int getMostOldSendMessageCacheSequence()
     * public Date getMostOldSendBufferTime()→public Date getMostOldSendMessageCacheTime()
  * 以下のメソッドを追加した。
     * public void setSendBindAddress(String)
     * public String getSendBindAddress()
     * public void setClientConnectMessageId(String)
     * public String getClientConnectMessageId()
     * public void setClientCloseMessageId(String)
     * public String getClientCloseMessageId()
     * public void reset()
128. jp.ossc.nimbus.service.publish.websocket.AbstractJMSMessageDispatcherServiceの変更
  * 以下のメソッドを追加した。
     * public long getMessageReceiveCount()
129. jp.ossc.nimbus.service.publish.websocket.AbstractPublishMessageDispatcherServiceの変更
  * WebSokcetの接続先URLの取得ロジックをDBや静的定義から取得出来るように変更した。
  * クラスタ構成の場合、リソース使用率の少ないサーバへ接続できるように変更した。
  * 以下のメソッドを追加した。
     * public int getMessageSendParameterRecycleListSize()
     * public void setMessageSendParameterRecycleListSize(int)
  * 以下のメソッドを削除した。
     * public long getMessageReceiveCount()
130. jp.ossc.nimbus.service.queue.AbstractDistributedQueueSelectorServiceの変更
  * サービスの停止時に、分散対象のキューを開放するように変更した。
  * 以下のメソッドを追加した。
     * protected void onNewKey(int, Object, Object)
  * 以下のメソッドのシグニチャを変更した。
     * protected double getQueueOrder(int)→protected double getQueueOrder(int, Object, Object)
131. jp.ossc.nimbus.service.queue.AsynchContextの変更
  * Cloneableインタフェースを実装した。
  * 以下のメソッドを追加した。
     * public void clearThreadContext()
132. jp.ossc.nimbus.service.queue.BeanFlowInvokerCallQueueHandlerServiceの変更
  * beanFlowInvokerFactoryがnullでもサービスが開始できるように変更した。
  * 以下のメソッドを追加した。
     * public boolean isClearThreadContext()
     * public void setClearThreadContext(boolean)
133. jp.ossc.nimbus.service.queue.DefaultQueueServiceの変更
  * Cacheサービスが設定されている場合、CacheされているオブジェクトをQueueに復元するように変更した。
  * 以下のメソッドを追加した。
     * public int getWaitCount()
134. jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerServiceの変更
  * 以下のメソッドを追加した。
     * public int getWaitCount()
     * public void setIgnoreNullElement(boolean)
     * public boolean isIgnoreNullElement()
135. jp.ossc.nimbus.service.queue.DistributedQueueServiceの変更
  * SharedQueueを使った分散処理ができるように変更した。
  * 以下のメソッドを追加した。
     * public int getWaitCount()
136. jp.ossc.nimbus.service.queue.Queueの変更
  * 以下のメソッドを追加した。
     * public int getWaitCount()
137. jp.ossc.nimbus.service.queue.QueueHandlerContainerServiceの変更
  * 以下のメソッドを追加した。
     * public int getWaitCount()
     * public void setStopWaitTimeout(long)
     * public long getStopWaitTimeout()
     * public void setIgnoreNullElement(boolean)
     * public boolean isIgnoreNullElement()
138. jp.ossc.nimbus.service.queue.SharedQueueServiceの変更
  * キューの取得性能を向上した。
  * 以下のメソッドを削除した。
     * public boolean isMultiThreadGet()
     * public void setMultiThreadGet(boolean isSafe)
     * public int getWaitCount()
     * public void setSeekDepth(int size)
     * public int getSeekDepth()
139. jp.ossc.nimbus.service.queue.ThreadLocalQueueServiceの変更
  * 以下のメソッドを追加した。
     * public int getWaitCount()
140. jp.ossc.nimbus.service.scheduler2.AbstractScheduleExecutorServiceの変更
  * ログ出力メッセージを変更した。
  * スケジュールの実行結果がDISABLEの場合、スケジュールを無効化するようにした。
  * 以下のメソッドを追加した。
     * public void setJournalServiceName(ServiceName)
     * public ServiceName getJournalServiceName()
     * public void setEditorFinderServiceName(ServiceName)
     * public ServiceName getEditorFinderServiceName()
     * public void setThreadContextServiceName(ServiceName)
     * public ServiceName getThreadContextServiceName()
141. jp.ossc.nimbus.service.scheduler2.AbstractSchedulerServiceの変更
  * ログ出力メッセージを変更した。
  * スケジュールの投入時に、OUTPUTを空にするように変更した。
  * 以下のメソッドを追加した。
     * public void setTimeServiceName(ServiceName)
     * public ServiceName getTimeServiceName()
142. jp.ossc.nimbus.service.scheduler2.BeanFlowScheduleExecutorServiceの変更
  * ログ出力メッセージを変更した。
143. jp.ossc.nimbus.service.scheduler2.CommandScheduleExecutorServiceの変更
  * スケジュールの入力を拡張して、作業ディレクトリ、環境変数、タイムアウト、終了条件等を指定できるようにした。
  * JSON形式の入力に対応した。
  * 以下のメソッドを追加した。
     * public void setCheckInterval(long)
     * public long getCheckInterval()
144. jp.ossc.nimbus.service.scheduler2.ConcentrateRequestの変更
  * 以下のメソッドを追加した。
     * public String toString()
145. jp.ossc.nimbus.service.scheduler2.ConcentrateResponseの変更
  * 以下のメソッドを追加した。
     * public String toString()
146. jp.ossc.nimbus.service.scheduler2.ScheduleManagerの変更
  * 以下のメソッドのシグニチャを変更した。
     * public List makeSchedule(Date, ScheduleMaster, String)→public List makeSchedule(Date, ScheduleMaster)
     * public List makeSchedule(Date, List, String)→public List makeSchedule(Date, List)
147. jp.ossc.nimbus.service.scheduler2.DefaultScheduleManagerServiceの変更
  * 以下のメソッドのシグニチャを変更した。
     * public List makeSchedule(Date, ScheduleMaster, String)→public List makeSchedule(Date, ScheduleMaster)
     * public List makeSchedule(Date, List, String)→public List makeSchedule(Date, List)
148. jp.ossc.nimbus.service.scheduler2.DatabaseScheduleManagerServiceの変更
  * findDependsSchedules(String)、findDependedSchedules(String)で、同一時間で、相互依存しているスケジュールが存在する場合に、自分のIDより前のスケジュールには依存しないように変更した。
  * 依存関係のエラー無視フラグを見て、エラーのジョブに依存せずにスケジュールを実行する機能を追加した。
  * ログ出力メッセージを変更した。
  * 以下のメソッドを追加した。
     * public ScheduleGroupDependsMasterTableSchema getScheduleGroupDependsMasterTableSchema()
     * public void setScheduleGroupDependsMasterTableSchema(ScheduleGroupDependsMasterTableSchema)
     * public ScheduleGroupTableSchema getScheduleGroupTableSchema()
     * public void setScheduleGroupTableSchema(ScheduleGroupTableSchema)
     * public ScheduleGroupDependsTableSchema getScheduleGroupDependsTableSchema()
     * public void setScheduleGroupDependsTableSchema(ScheduleGroupDependsTableSchema)
     * public void startControlStateCheck()
     * public boolean isStartControlStateCheck()
     * public void stopControlStateCheck()
     * public void startTimeoverCheck()
     * public boolean isStartTimeoverCheck()
     * public void stopTimeoverCheck()
     * public void setTimeServiceName(ServiceName)
     * public ServiceName getTimeServiceName()
  * 以下のメソッドのシグニチャを変更した。
     * public List makeSchedule(Date, ScheduleMaster, String)→public List makeSchedule(Date, ScheduleMaster)
     * public List makeSchedule(Date, List, String)→public List makeSchedule(Date, List)
149. jp.ossc.nimbus.service.scheduler2.Scheduleの変更
  * 以下のメソッドのシグニチャを変更した。
     * public String[] getDepends()→public ScheduleDepends[] getDepends()
     * public String getGroupId()→public String getGroupId(String)
     * public void setGroupId(String)→public void setGroupId(String, String)
  * 以下のメソッドを追加した。
     * public Map getGroupIdMap()
     * public ScheduleDepends[] getDependsInGroupMaster(String)
     * public Map getDependsInGroupMasterMap()
     * public ScheduleDepends[] getDependsInGroup(String)
     * public Map getDependsInGroupMap()
     * public ScheduleDepends[] getGroupDependsOnGroupMaster(String)
     * public Map getGroupDependsOnGroupMasterMap()
     * public ScheduleDepends[] getDependsOnGroup()
     * public ScheduleDepends[] getGroupDependsOnGroup(String)
     * public Map getGroupDependsOnGroupMap()
150. jp.ossc.nimbus.service.scheduler2.DefaultScheduleの変更
  * 以下のコンストラクタのシグニチャを変更した。
     * public DefaultSchedule(String,String[],Date,String,Object,String[],String,String)→public DefaultSchedule(String,String[],Date,String,Object,ScheduleDepends[],Map,ScheduleDepends[],Map,String,String)
     * public DefaultSchedule(String,String[],Date,String,Object,String[],String,String,long,Date,long)→public DefaultSchedule(String,String[],Date,String,Object,ScheduleDepends[],Map,ScheduleDepends[],Map,String,String,long,Date,long)
  * 以下のメソッドのシグニチャを変更した。
     * public String[] getDepends()→public ScheduleDepends[] getDepends()
     * public String getGroupId()→public String getGroupId(String)
     * public void setGroupId(String)→public void setGroupId(String, String)
  * 以下のメソッドを追加した。
     * public Map getGroupIdMap()
     * public ScheduleDepends[] getDependsInGroupMaster(String)
     * public Map getDependsInGroupMasterMap()
     * public ScheduleDepends[] getDependsInGroup(String)
     * public Map getDependsInGroupMap()
     * public ScheduleDepends[] getGroupDependsOnGroupMaster(String)
     * public Map getGroupDependsOnGroupMasterMap()
     * public ScheduleDepends[] getDependsOnGroup()
     * public ScheduleDepends[] getGroupDependsOnGroup(String)
     * public Map getGroupDependsOnGroupMap()
151. jp.ossc.nimbus.service.scheduler2.ScheduleMasterの変更
  * 以下のメソッドのシグニチャを変更した。
     * public String[] getDepends()→public ScheduleDepends[] getDepends()
  * 以下のメソッドを追加した。
     * public ScheduleDepends[] getDependsInGroup(String)
     * public Map getDependsInGroupMap()
     * public ScheduleDepends[] getDependsOnGroup()
     * public ScheduleDepends[] getGroupDependsOnGroup(String)
     * public Map getGroupDependsOnGroupMap()
152. jp.ossc.nimbus.service.scheduler2.DefaultScheduleMasterの変更
  * 以下のコンストラクタのシグニチャを変更した。
     * public DefaultScheduleMaster(String,String[],String,String,Object,Date,boolean,String[],String,String,boolean)
     *    * →public DefaultScheduleMaster(String,String[],String,String,Object,Date,boolean,ScheduleDepends[],ScheduleDepends[],String,String,boolean)
     * public DefaultScheduleMaster(String,String[],String,String,Object,Date,long,Date,long,boolean,String[],String,String,boolean)
     *    * →public DefaultScheduleMaster(String,String[],String,String,Object,Date,long,Date,long,boolean,ScheduleDepends[],ScheduleDepends[],String,String,boolean)
     * public DefaultScheduleMaster(String,String[],String,String,Object,Date,long,long,Date,long,boolean,String[],String,String,boolean)
     *    * →public DefaultScheduleMaster(String,String[],String,String,Object,Date,Date,long,long,Date,long,boolean,ScheduleDepends[],ScheduleDepends[],String,String,boolean)
  * 以下のメソッドのシグニチャを変更した。
     * public String[] getDepends()→public ScheduleDepends[] getDepends()
     * public void setDepends(String[])→public void setDepends(ScheduleDepends[])
  * 以下のメソッドを追加した。
     * public ScheduleDepends[] getDependsInGroup(String)
     * public Map getDependsInGroupMap()
     * public ScheduleDepends[] getDependsOnGroup()
     * public ScheduleDepends[] getGroupDependsOnGroup(String)
     * public Map getGroupDependsOnGroupMap()
     * public void setDepends(ScheduleDepends[])
     * public void setDependsInGroup(String, ScheduleDepends[])
     * public void setDependsOnGroup(ScheduleDepends[])
     * public void setGroupDependsOnGroup(String, ScheduleDepends[])
153. jp.ossc.nimbus.service.server.BeanFlowInvokerCallQueueHandlerServiceの変更
  * 以下のメソッドを追加した。
     * public void setRequestClass(Class)
     * public Class getRequestClass()
     * public void setResponseClass(Class)
     * public Class getResponseClass()
     * public void setServerSocketFactoryServiceName(ServiceName)
     * public ServiceName getServerSocketFactoryServiceName()
     * public void setSocketFactoryServiceName(ServiceName)
     * public ServiceName getSocketFactoryServiceName()
     * public void setServerSocketFactory(ServerSocketFactory)
     * public ServerSocketFactory getServerSocketFactory()
     * public void setSocketFactory(SocketFactory)
     * public SocketFactory getSocketFactory()
154. jp.ossc.nimbus.service.server.DefaultServerServiceの変更
  * 以下のメソッドを追加した。
     * public void setRequestClass(Class)
     * public Class getRequestClass()
     * public void setResponseClass(Class)
     * public Class getResponseClass()
     * public void setServerSocketFactoryServiceName(ServiceName)
     * public ServiceName getServerSocketFactoryServiceName()
     * public void setSocketFactoryServiceName(ServiceName)
     * public ServiceName getSocketFactoryServiceName()
     * public void setServerSocketFactory(ServerSocketFactory)
     * public ServerSocketFactory getServerSocketFactory()
     * public void setSocketFactory(SocketFactory)
     * public SocketFactory getSocketFactory()
     * public void setHandleAccept(boolean)
     * public boolean isHandleAccept()
155. jp.ossc.nimbus.service.server.Requestの変更
  * 以下のメソッドを追加した。
     * public void setRequestClass(Class)
     * public Class getRequestClass()
     * public void setResponseClass(Class)
     * public Class getResponseClass()
     * public void setServerSocketFactoryServiceName(ServiceName)
     * public ServiceName getServerSocketFactoryServiceName()
     * public void setSocketFactoryServiceName(ServiceName)
     * public ServiceName getSocketFactoryServiceName()
     * public void setServerSocketFactory(ServerSocketFactory)
     * public ServerSocketFactory getServerSocketFactory()
     * public void setSocketFactory(SocketFactory)
     * public SocketFactory getSocketFactory()
     * public void accept(SocketChannel)
     * public void setAccept(boolean)
     * public boolean isAccept()
     * public void setFirst(boolean)
     * public boolean isFirst()
156. jp.ossc.nimbus.service.server.Responseの変更
  * 以下のメソッドを追加した。
     * public void setRequestClass(Class)
     * public Class getRequestClass()
     * public void setResponseClass(Class)
     * public Class getResponseClass()
     * public void setServerSocketFactoryServiceName(ServiceName)
     * public ServiceName getServerSocketFactoryServiceName()
     * public void setSocketFactoryServiceName(ServiceName)
     * public ServiceName getSocketFactoryServiceName()
     * public void setServerSocketFactory(ServerSocketFactory)
     * public ServerSocketFactory getServerSocketFactory()
     * public void setSocketFactory(SocketFactory)
     * public SocketFactory getSocketFactory()
     * public void close()
157. jp.ossc.nimbus.service.soap.JaxRpcServiceFactoryServiceの変更
  * 以下のメソッドを追加した。
     * public String getWsdlPath()
     * public void setWsdlPath(String)
158. jp.ossc.nimbus.service.soap.PortFactoryの変更
  * 以下のメソッドを追加した。
     * public void setStubProperty(String, Object)
     * public Object getStubProperty(String)
     * public Map getStubPropertyMap()
159. jp.ossc.nimbus.service.system.OperationSystemの変更
160. jp.ossc.nimbus.service.system.OperationSystemServiceの変更
  * 以下のメソッドを追加した。
     * public long getUptimeInSeconds()
161. jp.ossc.nimbus.service.websocket.DefaultEndpointServiceの変更
  * WebSokcetの接続先URLの取得ロジックをDBや静的定義から取得出来るよう変更した。
  * クラスタ構成の場合、リソース使用率の少ないサーバへ接続できるよ変更した。
162. jp.ossc.nimbus.service.websocket.DefaultPingPongHandlerServiceの変更
  * 以下のメソッドを削除した。
     * public int getMaxRetryCount()
     * public void setMaxRetryCount(int)
163. jp.ossc.nimbus.service.writer.DateElementの変更
  * デフォルトの日付フォーマットを、"yyyy.MM.dd HH:mm:ss"から"yyyy.MM.dd HH:mm:ss.SSS"に変更した。
164. jp.ossc.nimbus.servlet.BeanFlowServletの変更
  * 以下の初期化パラメータを追加した。
     * JournalServiceName
     * EditorFinderServiceName
     * ValidateEditorFinderServiceName
     * ActionEditorFinderServiceName
     * ContextServiceName
  * 以下のメソッドのシグニチャを変更した。
     * processValidate(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,BeanFlowInvoker)→processValidate(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,BeanFlowInvoker,Journal)
     * protected boolean handleValidateException(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,Exception)→protected boolean handleValidateException(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,Journal,Exception)
     * protected boolean handleValidateError(HttpServletRequest,HttpServletResponse,BeanFlowServletContext)→protected boolean handleValidateError(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,Journal)
     * protected void processAction(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,BeanFlowInvoker)→protected void processAction(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,BeanFlowInvoker,Journal)
     * protected boolean handleActionException(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,Exception)→protected boolean handleActionException(HttpServletRequest,HttpServletResponse,BeanFlowServletContext,Journal,Exception)
165. jp.ossc.nimbus.servlet.DefaultBeanFlowSelectorServiceの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
166. jp.ossc.nimbus.servlet.InterceptorChainCallFilterの変更
  * 初期化パラメータInterceptorChainFactoryServiceNameを追加した。
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
  * InterceptorChainFactoryを利用する場合、getInterceptorChainList(Object)を呼び出していたのを、getInterceptorChain(Object)を呼び出すように変更した。
167. jp.ossc.nimbus.servlet.MappingBeanFlowSelectorServiceの変更
  * DefaultBeanFlowSelectorServiceを継承するように変更し、BeanFlowのマッピングが見つからない場合には、親クラスの処理に委譲するように変更した。
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
168. jp.ossc.nimbus.servlet.RestServletの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
169. jp.ossc.nimbus.servlet.ServiceManagerFactoryServletの変更
  * 初期化パラメータAttributeMaxLengthを追加した。
170. jp.ossc.nimbus.servlet.WebSocketAuthServletの変更
  * WebSokcetの接続先URLの取得ロジックをDBや静的定義から取得出来るように変更した。
  * クラスタ構成の場合、リソース使用率の少ないサーバへ接続できるように変更した。
171. jp.ossc.nimbus.util.converter.BeanExchangeConverterの変更
  * isFieldOnly(Class)、isAccessorOnly(Class)で、引数で指定された型がキャスト可能な型に対しても、設定が有効になるように変更した。
  * 以下のメソッドを追加した。
     * public void setDisabledPropertyNames(Class, String[])
     * public void setEnabledPropertyNames(Class, String[])
     * public boolean isEnabledPropertyName(Class, String)
172. jp.ossc.nimbus.util.converter.BeanJSONConverterの変更
  * isFieldOnly(Class)、isAccessorOnly(Class)で、引数で指定された型がキャスト可能な型に対しても、設定が有効になるように変更した。
  * プロパティがjsonオブジェクトの場合に、対象Beanのプロパティが取得可能なら、取得してみて、値が取得できた場合には、そのオブジェクトに読み込みを行うように変更した。
  * 以下のメソッドを追加した。
     * public void setDisabledPropertyNames(Class, String[])
     * public void setEnabledPropertyNames(Class, String[])
     * public boolean isEnabledPropertyName(Class, String)
     * public void setToUpperCase(boolean)
     * public boolean isToUpperCase()
     * public void setToLowerCase(boolean)
     * public boolean isToLowerCase()
173. jp.ossc.nimbus.util.converter.CustomConverterの変更
  * FormatConverterインタフェースを実装した。
  * 以下のメソッドを追加した。
     * public void setFormat(String)
174. jp.ossc.nimbus.util.converter.DataSetJSONConverterの変更
  * 以下のメソッドを追加した。
     * public void setCloneBindingObject(boolean)
     * public boolean isCloneBindingObject()
     * public void setOutputVTLTemplate(boolean)
     * public boolean isOutputVTLTemplate()
175. jp.ossc.nimbus.util.converter.DataSetServletRequestParameterConverterの変更
  * リクエストパスの評価方法を、"PathInfoまたはServletPath"から"ServletPathとPathInfoを連結したもの"に変更した。
176. jp.ossc.nimbus.util.net.GlobalUIDの変更
  * SerializableからExternalizableに変更した。
177. jp.ossc.nimbus.util.net.ServerSocketFactoryの変更
  * 以下のメソッドを追加した。
     * public void setSocketProperties(Map)
     * public void setSocketProperty(String, Object)
     * public Object getSocketProperty(String)
  * 以下のメソッドのシグニチャを変更した。
     * jp.ossc.nimbus.util.net.ServerSocket applyServerSocketProperties(jp.ossc.nimbus.util.net.ServerSocket)→java.net.ServerSocket applyServerSocketProperties(java.net.ServerSocket)
178. jp.ossc.nimbus.util.net.SocketFactoryの変更
  * 以下のメソッドを追加した。
     * public void setConnectionTimeout(int)
     * public int getConnectionTimeout()
179. jp.ossc.nimbus.util.sql.WrappedStatementの変更
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorder(PerformanceRecorder)
180. nimbus.util.sql.WrappedPreparedStatementの変更
  * 以下のメソッドを追加した。
     * public void setPerformanceRecorder(PerformanceRecorder)

### 機能追加

1. jp.ossc.nimbus.beans.FileArrayEditorを追加
  * java.io.File配列を編集するPropertyEditorインタフェースの実装クラスを新規追加した。
2. jp.ossc.nimbus.beans.SQLDateEditorを追加
  * java.sql.Dateを編集するPropertyEditorインタフェースの実装クラスを新規追加した。
3. jp.ossc.nimbus.beans.TimeEditorを追加
  * java.sql.Timeを編集するPropertyEditorインタフェースの実装クラスを新規追加した。
4. jp.ossc.nimbus.service.aop.interceptor.BeanFlowJournalMetricsInterceptorServiceを追加
  * 業務フローの呼び出しに対して、ジャーナル出力サイズのメトリクスを取得するインターセプタを新規追加した。
5. jp.ossc.nimbus.service.aop.interceptor.PerformanceRecordInterceptorServiceを追加
  * メソッドの呼び出しに対して、PerformanceRecorderでパフォーマンスを記録するインターセプタを新規追加した。
6. jp.ossc.nimbus.service.aop.interceptor.BeanFlowSelectCheckInterceptorServiceを追加
  * BeanFlowSelectorが選択した業務フローが存在するかをチェックするインターセプタを新規追加した。
7. jp.ossc.nimbus.service.aop.interceptor.BlockadeAllCloseExceptionを追加
  * 完全閉塞している場合にthrowされる例外を新規追加した。
8. jp.ossc.nimbus.service.aop.interceptor.BlockadePartCloseExceptionを追加
  * 部分閉塞している場合にthrowされる例外を新規追加した。
9. jp.ossc.nimbus.service.aop.interceptor.servlet.HttpResponseCacheInterceptorServiceを追加
  * HTTPのレスポンスをキャッシュするインターセプタを新規追加した。
10. jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletRequestURLConvertInterceptorServiceを追加
  * URLの一部をパラメータとして抜き出し、URLから除外するインターセプタを新規追加した。
11. jp.ossc.nimbus.service.beancontrol.BeanFlowCoverageを追加
  * 業務フローのカバレッジを取得する機能のインタフェースを新規追加した。
12. jp.ossc.nimbus.service.beancontrol.BeanFlowCoverageImplを追加
  * 業務フローのカバレッジを取得する機能のインタフェースBeanFlowCoverageの実装クラスを新規追加した。
13. jp.ossc.nimbus.service.beancontrol.BeanFlowCoverageRepoterを追加
  * 業務フローのカバレッジをレポートする機能のインタフェースを新規追加した。
14. jp.ossc.nimbus.service.beancontrol.ConsoleBeanFlowCoverageRepoterServiceを追加
  * 業務フローのカバレッジをコンソールにレポートするBeanFlowCoverageRepoterインタフェースの実装サービスを新規追加した。
15. jp.ossc.nimbus.service.beancontrol.HtmlBeanFlowCoverageRepoterServiceを追加
  * 業務フローのカバレッジをHTMLでレポートするBeanFlowCoverageRepoterインタフェースの実装サービスを新規追加した。
16. jp.ossc.nimbus.service.beancontrol.Compilerを追加
  * 業務フローのコンパイル機能を新規追加した。
17. jp.ossc.nimbus.service.connection.QuerySearchManagerを追加
  * データベースを検索する機能を新規追加した。
18. jp.ossc.nimbus.service.connection.DefaultQuerySearchManagerServiceを追加
  * データベースを検索し、結果をキャッシュするQuerySearchManagerインタフェースの実装サービスを新規追加した。
19. jp.ossc.nimbus.service.http.httpclient.MultipartPostHttpRequestImplを追加
  * マルチパートリクエスト用のHttpRequest実装を新規追加した。
20. jp.ossc.nimbus.service.io.KryoExternalizerServiceを追加
  * 直列化ライブラリKryoを使ったExternalizer実装サービスを新規追加した。
21. jp.ossc.nimbus.service.io.NimbusExternalizerServiceを追加
  * Nimbusの独自直列化処理を使ったExternalizer実装サービスを新規追加した。
22. jp.ossc.nimbus.service.jmx.MBeanServerConnectionFactoryを追加
  * JMX接続を取得する機能のインタフェースを新規追加した。
23. jp.ossc.nimbus.service.jmx.DefaultMBeanServerConnectionFactoryServiceを追加
  * JMX接続を取得するMBeanServerConnectionFactoryインタフェースのデフォルト実装サービスを新規追加した。
24. jp.ossc.nimbus.service.jmx.MBeanServerConnectionFactoryExceptionを追加
  * JMX接続の取得に失敗した場合に発生する例外クラスを新規追加した。
25. jp.ossc.nimbus.service.journal.editor.FormatConvertJournalEditorServiceを追加
  * フォーマットコンバーターを使用し、オブジェクトをフォーマットするJournalEditorインタフェースの実装サービスを新規追加した。
26. jp.ossc.nimbus.service.keepalive.http.HttpKeepAliveCheckerServiceを追加
  * Httpサーバの稼動状態をチェックするKeepAliveCheckerインタフェースの実装サービスを新規追加した。
27. jp.ossc.nimbus.service.keepalive.tcp.TcpKeepAliveCheckerServiceを追加
  * TCPソケットを開いて、サーバの稼動状態をチェックするKeepAliveCheckerインタフェースの実装サービスを新規追加した。
28. jp.ossc.nimbus.service.performance.PerformanceRecorderを追加
  * 処理性能を記録する機能のインタフェースを新規追加した。
29. jp.ossc.nimbus.service.performance.DefaultPerformanceRecorderServiceを追加
  * 処理性能を記録するPerformanceRecorderインタフェースのデフォルト実装サービスを新規追加した。
30. jp.ossc.nimbus.service.publish.ClusterClientConnectionFactoryを追加
  * クラスタ化されたClientConnection用のClientConnectionFactoryインタフェースを新規追加した。
31. jp.ossc.nimbus.service.publish.RemoteClusterClientConnectionFactoryを追加
  * ClusterClientConnectionFactoryのリモートオブジェクトとなるクラスを新規追加した。
32. jp.ossc.nimbus.service.publish.websocket.SessionIdMessageSendDistributedQueueSelectorServiceを追加
  * メッセージをセッションIDで振り分けるDistributedQueueSelectorインタフェースの実装サービスを追加した。
33. jp.ossc.nimbus.service.scheduler2.ScheduleDependsを追加
  * スケジュールの依存関係を示すインタフェースを新規追加した。
34. jp.ossc.nimbus.service.scheduler2.DefaultScheduleDependsを追加
  * スケジュールの依存関係を示すScheduleDependsインタフェースのデフォルト実装クラスを新規追加した。
35. jp.ossc.nimbus.service.server.ActionRequestを追加
  * クライアントからのアクション指定リクエストを格納するクラスを新規追加した。
36. jp.ossc.nimbus.service.server.StatusResponseを追加
  * クライアントへのステータス付きレスポンスを格納するクラスを新規追加した。
37. jp.ossc.nimbus.service.server.Servantを追加
  * クライアントに対応するサーバントを実装するインタフェースを新規追加した。
38. jp.ossc.nimbus.service.soap.WsServiceExceptionを追加
  * JAX-WSサービス関連例外クラスを新規追加した。
39. jp.ossc.nimbus.service.soap.WsPortFactoryServiceを追加
  * JAX-WSのPortFactoryインタフェース実装サービスを新規追加した。
40. jp.ossc.nimbus.service.soap.WsServiceFactoryを追加
  * JAX-WSのServiceを生成する機能のインタフェースを新規追加した。
41. jp.ossc.nimbus.service.soap.WsServiceFactoryServiceを追加
  * WsServiceFactoryインタフェースの実装サービスを新規追加した。
42. jp.ossc.nimbus.service.soap.WsServiceJournalHandlerServiceを追加
  * ジャーナルを出力するjavax.xml.ws.handler.soap.SOAPHandlerインタフェースの実装サービスを新規追加した。
43. jp.ossc.nimbus.service.soap.WsServiceMetricsHandlerServiceを追加
  * 性能統計を取得するjavax.xml.ws.handler.soap.SOAPHandlerインタフェースの実装サービスを新規追加した。
44. jp.ossc.nimbus.service.system.HostResolverを追加
  * ホスト名を解決する機能のインタフェースを新規追加した。
45. jp.ossc.nimbus.service.system.DefaultHostResolverServiceを追加
  * サービス定義からホスト名を解決するHostResolverインタフェースのデフォルト実装サービスを新規追加した。
46. jp.ossc.nimbus.service.system.DatabaseHostResolverServiceを追加
  * データベースからホスト名を解決するHostResolverインタフェースのデフォルト実装サービスを新規追加した。
47. jp.ossc.nimbus.service.system.Timeを追加
  * 時刻を取得する機能のインタフェースを新規追加した。
48. jp.ossc.nimbus.service.system.DefaultTimeServiceを追加
  * 時刻を取得するTimeインタフェースのデフォルト実装サービスを新規追加した。
49. jp.ossc.nimbus.service.template.TemplateEngineを追加
  * テンプレートから動的に文字列を生成する機能のインタフェースを新規追加した。
50. jp.ossc.nimbus.service.template.VelocityTemplateEngineServiceを追加
  * Apache Velocityを使ったTemplateEngineインタフェースの実装サービスを新規追加した。
51. jp.ossc.nimbus.service.template.TemplateTransformExceptionを追加
  * テンプレート変換に失敗した際に発生する例外クラスを新規追加した。
52. jp.ossc.nimbus.service.testパッケージを追加
  * テストフレームワークのコアとなるクラス群のパッケージを新規追加した。
53. jp.ossc.nimbus.service.test.actionパッケージを追加
  * テストフレームワークのテストアクションのクラス群のパッケージを新規追加した。
54. jp.ossc.nimbus.service.test.evaluateパッケージを追加
  * テストフレームワークの評価アクションのクラス群のパッケージを新規追加した。
55. jp.ossc.nimbus.service.test.proxyパッケージを追加
  * テストフレームワークのプロキシのクラス群のパッケージを新規追加した。
56. jp.ossc.nimbus.service.test.proxy.netcrusherパッケージを追加
  * テストフレームワークのプロキシのNetCrusherを利用した実装クラス群のパッケージを新規追加した。
57. jp.ossc.nimbus.service.test.reportパッケージを追加
  * テストフレームワークのテスト結果レポートのクラス群のパッケージを新規追加した。
58. jp.ossc.nimbus.service.test.resourceパッケージを追加
  * テストフレームワークのテストリソースのクラス群のパッケージを新規追加した。
59. jp.ossc.nimbus.service.test.stub.httpパッケージを追加
  * テストフレームワークのHTTPスタブのクラス群のパッケージを新規追加した。
60. jp.ossc.nimbus.service.test.swingパッケージを追加
  * テストフレームワークのSwing GUIのクラス群のパッケージを新規追加した。
61. jp.ossc.nimbus.servlet.JMXConsoleServletを追加
  * JMXコンソールを表示するサーブレットを新規追加した。
62. jp.ossc.nimbus.util.converter.HttpServletRequestFileConverterを追加
  * マルチパートでファイルアップロードされたリクエストを変換するConverter実装クラスを新規追加した。
63. jp.ossc.nimbus.util.converter.RecordCSVConverterを追加
  * レコード⇔CSVの相互変換を行うConverter実装クラスを新規追加した。
64. jp.ossc.nimbus.util.converter.RecordListCSVConverterを追加
  * レコードリスト⇔CSVの相互変換を行うConverter実装クラスを新規追加した。
65. nimbus.util.converter.SQLDateConverterを追加
  * java.util.Date⇔java.sql.Date、Time、Timestampの相互変換を行うConverter実装クラスを新規追加した。

