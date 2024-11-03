+ StartupFile {

	*choose {
		"%: renamed to StartupFile.dialog...".postln;
		this.dialog
	}

	// numToShow can be used to try different numbers of files/buttons
	*dialog { |numToShow|
		var w, dropper, nameView, saveBut, buttons, openButtons;
		var onCol = Color.green;
		var offCol = Color.grey(0.62);
		var namesToShow, numColumns, numRows;

		this.updateFiles;
		// prep for layout
		numToShow = numToShow ? this.fileNames.size;
		namesToShow = this.fileNames.wrapExtend(numToShow);
		numColumns = (namesToShow.size / 6).sqrt.roundUp.asInteger.max(1);
		numRows = (numToShow / numColumns).roundUp.asInteger;

		// "numColumns: %, numRows: % \n\n".postf(numColumns, numRows);

		w = Window("Startup files",
			Rect.aboutPoint(Window.screenBounds.center,
				numColumns * 75,
				numRows + 4 * 15
			)
		);
		w.view.minSize_(Size(150, 170));
		w.view.maxSize_(Size(*Window.screenBounds.extent.asArray));
		w.layout = VLayout().margins_(4).spacing_(2);

		dropper = DragSink(w).object_("*** drop file here ***")
		.receiveDragHandler_ { |ds|
			var drag = View.currentDrag;
			var pathSep = Platform.pathSeparator;
			var filename;
			"*** drop got % \n".postf(drag);
			if (drag.isKindOf(String) and: { drag.first == pathSep }) {
				ds.object = drag;
				filename = drag.basename.splitext.first;
				ds.string = filename;
				nameView.string = filename;
			};
		};

		nameView = TextView(w).string_("enter name")
		.minSize_(Size(100,20)).maxSize_(Size(200,60));

		saveBut = Button(w).states_([["SAVE"]]).maxSize_(Size(60,40))
		.action_({
			var path = dropper.object;
			if (path.endsWith("scd").not) {
				"*** StartupFile: % is not an scd file!\n".postf(path.cs)
			} {
				StartupFile.writeStartupFileToExternalPath(
					nameView.string,
					dropper.object
				);
				w.close;
				this.dialog;
			};
		});

		buttons = namesToShow.collect { |name|
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

		w.layout.add(
			VLayout(
				dropper,
				HLayout(nameView,  saveBut)
			),
			Button(w).states_([["Open startup_files dir"]])
			.action_({ this.filesDir.openOS })
		);
		w.layout.add(
			StaticText(w).align_('center').maxSize_(Size(400, 60))
			.string_("Click to select, shift-click to edit, Alt-Click to reboot:")
		);

		w.layout.add(
			GridLayout.columns(*buttons.clump(numRows))
		);
		w.front;
		^w
	}
}