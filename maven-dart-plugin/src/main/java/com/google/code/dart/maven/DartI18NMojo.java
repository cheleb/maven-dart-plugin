package com.google.code.dart.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 * 
 */
@Mojo(name = "i18n", defaultPhase = LifecyclePhase.COMPILE)
@Execute(goal = "i18n", lifecycle = "web")
public class DartI18NMojo extends AbstractDartMojo {

	@Parameter(required = true, defaultValue = "${project.build.directory}/m2e-wtp/web-resources/dart2js")
	private File outputDirectory;

	@Parameter
	private File dartFile;

	@Parameter
	private File[] dartFiles;

	public void execute() throws MojoExecutionException {

		getLog().info("DART_HOME:" + dartHome);

		if (dartFile != null && dartFiles != null) {
			throw new MojoExecutionException(
					"Only one of \"dartFile\" or \"dartFiles\" parameter can be specified");
		}

		if (dartFile == null && (dartFiles == null || dartFiles.length == 0)) {
			throw new MojoExecutionException(
					"At least one dart file must be specified");
		}

		if (outputDirectory.exists() == false) {
			if (outputDirectory.mkdirs()) {
				getLog().info(
						"Create output dir: "
								+ outputDirectory.getAbsolutePath());
			}
		}

		if (dartFile != null) {
			dartFiles = new File[] { dartFile };
		}

		for (File file : dartFiles) {
			getLog().info("Dart file:" + file.toString());
			ProcessBuilder processBuilder = new ProcessBuilder(
					dartHome.getAbsolutePath() + "/bin/dart2js", "-o"
							+ outputDirectory.getAbsolutePath() + "/"
							+ file.getName(), file.getAbsolutePath());
			System.out.println(processBuilder.command());
			try {
				Process process = processBuilder.start();

				int waitFor = process.waitFor();
				if (waitFor != 0) {
					try (BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(process.getInputStream()))) {
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							getLog().info(line);
						}
						throw new MojoExecutionException("dart2js failed");
					}
				}
				getLog().info("Process ended: " + +waitFor);
			} catch (IOException e) {
				throw new MojoExecutionException(file.getName(), e);
			} catch (InterruptedException e) {
				throw new MojoExecutionException(file.getName(), e);
			}
		}

		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}

	}
}
