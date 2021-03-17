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
