package com.google.code.dart.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractDartMojo extends AbstractMojo {

	@Parameter(defaultValue = "${env.DART_HOME}", property = "dart.home")
	protected File dartHome;

	public AbstractDartMojo() {
		super();
	}

}