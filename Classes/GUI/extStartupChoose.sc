+ StartupFile {

	*choose {
		"%: renamed to StartupFile.dialog...".postln;
		this.dialog
	}

	*dialog {
		var w, dropper, nameView, saveBut, buttons, openButtons;
		var onCol = Color.green;
		var offCol = Color.grey(0.62);

		this.updateFiles;
		w = Window("Startup files",
			Rect.aboutPoint(Window.screenBounds.center, 50, 150)
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
		w.layout = VLayout().margins_(4).spacing_(2);

		dropper = DragSink(w).object_("*** drop file here ***")
		.receiveDragHandler_ { |ds|
			var drag = View.currentDrag.postcs;
			var pathSep = Platform.pathSeparator;
			if (drag.isKindOf(String) and: { drag.first == pathSep }) {
				drag.postcs;
				ds.object = drag;
				ds.string = drag.basename.splitext.first;
			};
		};
		nameView = TextView(w).string_("enter name")
		.minSize_(Size(60,20));
		saveBut = Button(w).states_([["SAVE"]]).action_({
			var path = dropper.object;
			if (path.endsWith("scd").not) {
				"*** StartupFile: % is not an scd file!\n".postf(path.cs)
			} {
				StartupFile.writeStartupFileToExternalPath(
					nameView.string,
					dropper.object
				);
			};
		});

		w.layout.add(
			VLayout(
				dropper,
				HLayout(nameView,  saveBut)
			),
			Button(w).states_([["Open startup_files dir"]])
			.action_({ this.filesDir.openOS })
		);
		w.layout.add(
			StaticText(w).align_('center')
			.string_("Click to select,\nAlt-Click to reboot:")
		);
		w.layout.add(
			HLayout(
				VLayout(*buttons),
				VLayout(*openButtons)
			)
		);
		w.front;
		^w
	}
}