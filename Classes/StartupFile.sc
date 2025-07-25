
StartupFile {

	classvar <startupPath, <dirname = "startup_files", <filesDir, <examplesDir;
	classvar <filePaths, <fileNames, <pathsDict;
	classvar <currentName, <currentPath;
	classvar <redirectText, <fileExt = ".scd";

	*defaultPath { ^thisProcess.platform.startupFiles.last }

	*initClass {
		startupPath = this.defaultPath;
		filesDir = (startupPath.dirname +/+ dirname);
		examplesDir = this.filenameSymbol.asString.dirname.dirname +/+ "startupFileExamples";

		if (filesDir.pathMatch.isEmpty) {
			File.mkdir(filesDir);

			this.copyExamples;
		};

		pathsDict = ();

		this.updateFiles;

		redirectText = "/****\n"
		"/// Redirection file written by StartupFile.\n"
		"/// open destination file with:\n"
		"Document.open(%); // or\n"
		"StartupFile.open(%);\n"
		"/// select or open startup files:\n"
		"StartupFile.dialog;\n"
		"***************************/\n\n"
		"StartupFile.redirectLoad(%);";
	}

	*updateFiles {
		pathsDict.clear;
		filePaths = (filesDir +/+ "*.scd").pathMatch;
		fileNames = filePaths.collect { |path|
			var name = path.basename.splitext.first.asSymbol;
			pathsDict.put(name, path);
			name
		};
		"---\n// StartupFile found % files; to choose or open one, do:\n"
		.postf(fileNames.size);
		"StartupFile.dialog;".postln;
		"---".postln;
	}

	*copyExamples {
		var filePaths = pathMatch(examplesDir +/+ "*.scd");
		// "% : % example filePaths found.\n".postf(this, filePaths.size);
		filePaths.do { |path|
			path = path.escapeChar($ );
			// allow force copy for updates?
			// cp -n: copy, do not overwrite existing file
			systemCmd("cp -n % %"
				.format(path,
					(filesDir +/+ path.basename).escapeChar($ )
				)
			);
		};
	}

	*findPath { |name, action|
		var path = pathsDict[name];
		if (path.isNil) {
			"% - no path for %.\n".postf(this, name)
		} { action.value(path) };
		^path
	}

	*open { |name| Document.open(this.findPath(name ? currentName)) }

	*redirectLoad { |name|
		this.findPath(name, { |path|
			"*** %: loading %\n\n".postf(this, path.basename);
			currentName = name;
			currentPath = path;
			path.standardizePath.load;
		})
	}

	*openDefault { startupPath.openOS }
	*openDir { filesDir.openOS }

	*backupCurrentIfNeeded {
		var text, backupPath;
		if (File.exists(startupPath).not) {
			^this
		};
		text = File.readAllString(startupPath);
		if (text.contains("StartupFile.redirectLoad").not) {
			// need to write backup
			backupPath = filesDir +/+ "startup_" ++ Date.getDate.stamp ++ fileExt;
			"*** Backing up startup file to: \n%\n".postf(backupPath.cs);
			// text.postcs;
			File.use(backupPath, "w", { |f| f.write(text) });
			this.updateFiles;
		};
	}

	*writeRedirectFile { |name, check = true|
		var text, pathToRedirectTo = pathsDict[name];
		if (pathToRedirectTo.isNil) {
			"*** % : no file found for name %, cannot redirect.\n".postf(this, name.cs);
			^this
		};
		this.backupCurrentIfNeeded;
		"*** % : writing redirect to %.\n".postf(this, name.cs);
		text = redirectText.format(pathToRedirectTo.cs, name.cs, name.cs);
		File.use(this.defaultPath, "w", { |f| f.write(text) });
	}

	*writeStartupFileToExternalPath { |name, pathToLoad|
		var newStartupFilePath = filesDir +/+ name ++ fileExt;
		var text = "// StartupFile that redirects to an externalPath:\n"
		"/*\n Document.open(%);\n*/\n"
		"%.standardizePath.loadPaths;".format(pathToLoad.cs, pathToLoad.cs);
		if (File.exists(newStartupFilePath)) {
			"*** StartupFile % exists already!\n"
			"Please decide which one to keep etc.".postf(name.cs);
		} {
			File.use(newStartupFilePath, "w", { |f| f.write(text) });
		};
		this.updateFiles;
	}

}
