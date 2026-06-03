/*
 * Copyright 2026 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.commons.bootstrap.scan.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * スキャンパッケージ指定用アノテーションです.<br>
 * フレームワーク や アプリ のスキャンパッケージを指定する場合使われてます。<br>
 *
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BootstrapScan {
	String[] packages();
}
