/*
 * Copyright 2026 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.commons.bootstrap;

import jp.co.kccs.greenearth.commons.bootstrap.listener.GApplicationBootstrapListener;
import jp.co.kccs.greenearth.commons.bootstrap.listener.GApplicationBootstrapListenerImpl;
import jp.co.kccs.greenearth.commons.bootstrap.listener.GFrameworkBootstrapListener;
import jp.co.kccs.greenearth.commons.bootstrap.listener.GFrameworkBootstrapListenerImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * ブーツトラップのライフサイクルを管理するオーケストラータの実装クラスです.<br>
 *
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public class GBootstrapLifecycleOrchestratorImpl implements GBootstrapLifecycleOrchestrator{

	private GFrameworkBootstrapListener frameworkBootstrapListener;
	private GApplicationBootstrapListener applicationBootstrapListener;
	private GBootstrapState bootstrapState;

	/**
	 * コンストラクターです.<br>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 */
	public GBootstrapLifecycleOrchestratorImpl() {
		frameworkBootstrapListener = new GFrameworkBootstrapListenerImpl();
		applicationBootstrapListener = new GApplicationBootstrapListenerImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bootstrap() {
		try {
			bootstrapState = GBootstrapState.BOOTING;
			System.out.println("[BOOTING] -... Initializing Environment");
			frameworkBootstrapListener.initialize();
			bootstrapState = GBootstrapState.FRAMEWORK_COMPONENTS_INITIALIZED;
			System.out.println("[" + getState() + "] - Framework components OK.");
			applicationBootstrapListener.initialize();
			bootstrapState = GBootstrapState.APPLICATION_COMPONENTS_INITIALIZED;
			System.out.println("[" + getState() + "] - Application components OK.");
		} catch (Exception e) {
			bootstrapState = GBootstrapState.FAILED;
			System.out.println("[" + getState() + "] - Critical Error Encountered.");
			System.out.println("[PHASE] : " + getState());
			System.out.println("[CAUSE] : " + e.getMessage());
			System.out.println("[TRACE] : " + ExceptionUtils.getStackTrace(e));
			throw e;
		}
		bootstrapState = GBootstrapState.COMPLETED;
		System.out.println("[" + getState() + "] - Bootstrap squeence finish. System ready.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		applicationBootstrapListener.destroy();
		frameworkBootstrapListener.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GBootstrapState getState() {
		return bootstrapState;
	}
}
