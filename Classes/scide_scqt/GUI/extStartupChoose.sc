+ StartupFile {

	*choose {
		var w, buttons, openButtons;
		var onCol = Color.green;
		var offCol = Color.grey(0.62);

		this.updateFiles;
		w = Window("Choose startup file:",
			Rect.aboutPoint(Window.screenBounds.center, 100, 100)
		);
		buttons = this.fileNames.collect { |name|
			var col = if (name == currentName, onCol, offCol);
			Button(w).states_([[name, nil, col]]).action_({
				this.writeRedirectFile(name);
				currentName = name;
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