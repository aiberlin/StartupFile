

LoadMe {
	classvar <>postTime = true;
	classvar <globalStartTime, <totalTime;

	*start { |comment=""|
		"\n*** LoadMe starts --- %\n\n".postf(comment);
		globalStartTime = Main.elapsedTime;
	}

	*end { |comment=""|
		totalTime = Main.elapsedTime - globalStartTime;
		"*** % --- done in % seconds. %\n\n".postf(this, totalTime.round(0.01), comment);
	}

	*new { |filename, dir, server, preText = "", postText = ""|
		var path, paths;
		var doSync = server.notNil and: { server.serverRunning } and: { thisThread.isKindOf(Routine) };
		if (filename.isNil) {
			warn("%: path was nil! not loading.".format(this));
			^nil
		};

		path = filename.standardizePath;
		// if path is relative:
		if (path[0].isPathSeparator.not) {
			dir = dir ?? { thisProcess.nowExecutingPath.dirname };
			path = (dir +/+ path);
		};

		paths = path.pathMatch;
		if (paths.size == 0) {
			"*** no files found for % :\n".postf(path.cs);
			^nil
		};

		if (paths.size > 1) {
			"****** % matching files for % :\n".postf(paths.size, path.cs)
		};

		^paths.collect { |path|
			var t0 = Main.elapsedTime, loadDur;
			var result;
			"*** loading % : %".postf(path.basename, preText);
			result = path.load;

			if (doSync ) { server.sync };
			if (postTime) {
				loadDur = (Main.elapsedTime - t0).round(0.001);
				"... in % secs. %\n".postf(loadDur, postText);
			};
			result;
		}.unbubble;
	}
}
