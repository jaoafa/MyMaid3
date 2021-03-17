/*
 * jaoLicense
 *
 * Copyright (c) 2021 jao Minecraft Server
 *
 * The following license applies to this project: jaoLicense
 *
 * Japanese: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE.md
 * English: https://github.com/jaoafa/jao-Minecraft-Server/blob/master/jaoLICENSE-en.md
 */

package com.jaoafa.MyMaid3.Lib;

public interface CommandPremise {

    /**
     * コマンドを説明する文章を指定・返却します
     *
     * @return コマンドを説明する文章
     */
    String getDescription();

    /**
     * コマンドの使い方を指定・返却します。
     *
     * @return コマンドの使い方
     */
    CmdUsage getUsage();
}
