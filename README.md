# なにこれ？？
マイクラでチームPvPができるBukkit/Spigotプラグインです。<br>
マイクラのバージョンは1.8.8です。<br>
# ダウンロード
- [プラグイン本体](https://github.com/natyu192/KokuminTeamPvP/releases/latest)
- [サンプルマップ](https://natyu192.github.io/Kokumin%20Space.zip)
# 前提プラグイン
- [KokuminCore](../../../KokuminCore/releases/latest)
### 入れると良いもの
- [LunaChat](https://www.spigotmc.org/resources/lunachat.82293/) --- チャットがローマ字から日本語に変換されるようになります。
- [WorldEdit](https://dev.bukkit.org/projects/worldedit) --- コンパスでテレポートできるようになります。
# マップについて
- [マップ読み込みに関する説明](https://kokuminteampvp.readthedocs.io/ja/latest/tutorial/first.html)
- [マップの設定に関する説明](https://kokuminteampvp.readthedocs.io/ja/latest/)
# 使い方
### 最初にやること
1. `plugins/` フォルダに本プラグインと前提プラグインを入れる。
2. `plugins/KokuminTeamPvP/maps/` フォルダにマップを入れる。
3. サーバーを起動する！
### コマンド
|コマンド|説明|パーミッション|エイリアス|
|---|---|---|---|
|`/stats [player]`|戦績を表示する。|||
|`/maps`|マップの一覧を表示する。||`/rotation`<br>`/rot`|
|`/gamestart [秒数]`|ゲームを開始する。|`kokuminteampvp.start`||
|`/gameend [チームID]`|ゲームを終了する。<br>チームを指定すると、そのチームの勝利になる。|`kokuminteampvp.end`||
|`/cancelstart`|ゲーム開始のカウントダウンをストップする。|`kokuminteampvp.cancelstart`||
|`/cancelcycle`|ゲーム終了後、次のマップに移動するまでの<br>カウントダウンをストップする。|`kokuminteampvp.cancelcycle`||
|`/cycle [秒数]`|ゲーム終了後、次のマップに移動する。|`kokuminteampvp.cycle`||
|`/skipthismap [秒数]`|ゲーム開始前に、現在のマップをスキップして、<br>次のマップに移動する。|`kokuminteampvp.skipthismap`||
|`/setnext [マップ名]`|次のマップを指定する。|`kokuminteampvp.setnext`||
|`/gameadmin`|その他の管理者用コマンドをまとめたもの。|`kokuminteampvp.gameadmin`|`/ga`|
|`/match`|試合の進行状況を表示する。|||
|`/staff`|スタッフモードを切り替える。|`kokuminteampvp.staff`|`/mod`|
### パーミッションノード（権限）
|ノード|説明|
|---|---|
|`kokuminteampvp.staff`|スタッフモードになれたりする。|
|`kokuminteampvp.premium`|参加するときにチームを選べるようになる。|
