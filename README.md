# MyMaid3
[![Build Status](https://travis-ci.org/jaoafa/MyMaid3.svg?branch=master)](https://travis-ci.org/jaoafa/MyMaid3)
[![Support jdk](https://img.shields.io/badge/Support%20jdk-oraclejdk8-red.svg)](https://img.shields.io)
[![Author](https://img.shields.io/badge/Author%20MinecraftID-mine__book000-orange.svg)](https://img.shields.io)
[![License](https://img.shields.io/badge/license-None-yellow.svg)](https://img.shields.io)
[![jao Minecraft Server](https://raw.githubusercontent.com/jaoafa/jao-Minecraft-Server/master/logo/new_logo-421x97.png)](https://jaoafa.com)

[Click here for English README](https://github.com/jaoafa/MyMaid3/blob/master/README-en.md)

このプロジェクトは、今まで[jaoafa/MyMaid2(jaoafa/MyMaid2)](https://github.com/jaoafa/MyMaid2)で開発されてきた「MyMaid2」の後継にあたる「MyMaid3プラグイン」のソースコード公開場所です。
[jao Minecraft Server](https://jaoafa.com/)に関係するサーバのみで使用され、それ以外でのサーバでの使用は原則禁止しています。

## ライセンス
ライセンスは未指定(No License)です。つまりデフォルトの著作権法が適用されるため、一部、もしくはすべての複製、改変、再頒布を認めません。
(GitHubのForkについてはGitHubの利用規約の関係上、例外とします。)
本来であれば、ライセンス未指定の場合は改変されていない状態で使用する分には許可、とされていますが、上記の禁止記載により、別に許可された場合を除き**指定されたサーバ以外での使用は認められません。**
**ソースコードの利用、アイディアの利用等についても、jao Minecraft Serverで許可を取ってから使用するようにしてください。**

### これらを決めている理由
私が、このような「全禁止」という対応を取っている理由として、私が「自分の知らない場所で、自分の作成物を使用されたくない」という考えから決めているものです。
過去に、私の作成物を悪用したり、機能のアイディアをパクられたケースがいくつかありました。私に限らず、jao Minecraft Serverの管理部・開発部は「サーバ特有の文化を他の場所で使用されると、“サーバの特徴”が薄れたり、予測していなかった問題が発生する恐れがある」と考えています。
ただし、管理部・開発部内で話し合ったうえで、許可される場合がないわけではありません。本当に必要があれば管理部・開発部にお問い合わせください。

## プログラムについて
ここで公開されているプログラムのソースコードには、多分なにかしらの瑕疵やバグが存在します。しかし、開発者およびjao Minecraft Serverの管理部・開発部はそれらの瑕疵やバグをなるべく除去する努力義務を負いますが、それらによって生じた一切の問題についての**責任を負いません。**
また、利用者はこのプラグインに実装されている全ての機能及びプログラムなどをjao Minecraft Serverの管理部・開発部の許可なく他の場所において、一部もしくは全部を使用することはできません。Discordなどを通じて、明確に許可を取った上で、許可された範囲内で利用してください。
それらのバグ等を見つけられた場合は、[Issues](./issues)から新しいIssueを立ててくださるととても助かります。その際、後に記載する「Issueについて」をご覧ください

## Issueについて
当プロジェクトでは、利用者からのIssueを受け付けています。Issueの内容としては、「新機能の提案」・「バグ報告」・「疑問点」・「既存の機能の強化希望」等を行うのが適切と考えています。
テンプレートを以下においておくので、もしよろしければご利用ください。

```markdown
## Issue Type (Issueの種別)
  - [ ] バグ報告
  - [ ] 新機能提案
  - [ ] 既存機能の強化
  - [ ] 疑問

## Description (概要)

## Source code etc (ソースコードなど)

## Steps to Reproduce (再現の手順)

## ScreenShot etc (スクリーンショットなど)

## Related files (関連するファイル)

```

上記のテンプレートを、以下の要項に従って埋めてください。
  - 「Issue Type (Issueの種別)」では、そのIssueの種別を指定してください。``[x]``とすることでチェックボックスにチェックを入れることができます。
  - 「Description (概要)」では、内容の簡単な概要を記載してください。「～コマンドを実行したら、○○というエラーが発生した。」・「○○という機能に○○をつけてみたらどうだろうか」などで構いません。
  - 「Source code etc (ソースコードなど)」では、新機能や既存機能の強化時、バグ報告時に、新機能を実装するうえでの細かな内部動作のソースコード(Javaでなくても構いません)、既存機能の強化を行う際の改善されたソースコード(Javaでなくても構いません)の提示、どの部分のプログラムが問題を起こしているか、などがプログラムソースコードやコマンドとして記載されていると、より細かく利用者の思い通りに実装ができるようになります。
  - 「Steps to Reproduce (再現の手順)」では、主にバグを再現するための手順を細かく指定してください。実行したコマンド、実行した場所等が細かく記載されているとより再現ができる場合があります。
  - 「ScreenShot etc (スクリーンショットなど)」では、バグ発生時のスクリーンショット等を表記してください。
  - 「Related files (関連するファイル」では、それらのバグなどが発生した箇所や、機能強化時にどのファイルを変更したらいいか、分かる場合のみ記載してください。参考にいたします。

## Pull Requestについて

当プロジェクトでは基本的にPull Requestの機能を活用するような方針で運用しておりません。
Pull Requestの機能を無効化することは出来ないため放置しておりますが、Pull Requestのマージ等は原則的に行いませんし、それらに対して反応することも極力する気はございませんのでやめてください。正直、非常に迷惑です。
意見等はIssueまたはDiscordの専用チャンネルにて。
