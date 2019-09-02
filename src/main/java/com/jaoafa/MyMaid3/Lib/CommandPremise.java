package com.jaoafa.MyMaid3.Lib;

import java.util.List;

public interface CommandPremise {

	/**
	 * コマンドを説明する文章を指定・返却します
	 * @return　コマンドを説明する文章
	 */
	public String getDescription();

	/**
	 * コマンドの使い方を指定・返却します。
	 * @return コマンドの使い方
	 */
	public List<String> getUsage();
}
