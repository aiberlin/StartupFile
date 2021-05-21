+ StartupFile {

	*choose {
		"%: renamed to StartupFile.dialog...".postln;
		this.dialog
	}

	*dialog {
		var w, buttons, openButtons;
		var onCol = Color.green;
		var offCol = Color.grey(0.62);

		this.updateFiles;
		w = Window("Choose startup file:",
			Rect.aboutPoint(Window.screenBounds.center, 100, 100)
		);
		buttons = this.fileNames.collect { |name|
			var col = if (name == currentName, onCol, offCol);
			Button(w).states_([[name, nil, col]]).action_({ |bt, mod = 0|
				this.writeRedirectFile(name);
				currentName = name;
				if (mod.isAlt) {
					thisProcess.recompile;
				};
				w.close
			})
		};
		openButtons = this.fileNames.collect { |name|
			Button(w).states_([[\open]]).action_({
				this.open(name);
			})
		};
		w.layout = HLayout(
			VLayout(*buttons),
			VLayout(*openButtons)
		);
		w.front;
	}
}