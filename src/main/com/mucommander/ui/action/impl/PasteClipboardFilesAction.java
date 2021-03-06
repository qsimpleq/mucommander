/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2012 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.ui.action.impl;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.commons.runtime.OsFamily;
import com.mucommander.job.CopyJob;
import com.mucommander.utils.text.Translator;
import com.mucommander.ui.action.*;
import com.mucommander.ui.dialog.file.FileCollisionDialog;
import com.mucommander.ui.dialog.file.ProgressDialog;
import com.mucommander.ui.dnd.ClipboardNotifier;
import com.mucommander.ui.dnd.ClipboardSupport;
import com.mucommander.ui.main.MainFrame;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * This action pastes the files contained by the system clipboard to the currently active folder.
 * Does nothing if the clipboard doesn't contain any file.
 *
 * <p>Under Java 1.5 and up, this action gets automatically enabled/disabled when files are present/not present
 * in the clipboard.
 *
 * @author Maxence Bernard
 */
public class PasteClipboardFilesAction extends MuAction {

    PasteClipboardFilesAction(MainFrame mainFrame, Map<String, Object> properties) {
        super(mainFrame, properties);

        // Allows this action to be dynamically enabled when the clipboard contains files, and disabled otherwise.
        // ClipboardNotifier does not work under Mac OS X (tested under Tiger with Java 1.5.0_06)
        if(!OsFamily.MAC_OS_X.isCurrent())
            new ClipboardNotifier(this);
    }

    @Override
    public void performAction() {
        // Retrieve clipboard files
        FileSet clipboardFiles = ClipboardSupport.getClipboardFiles();
        if (clipboardFiles == null || clipboardFiles.isEmpty())
            return;

        // Start copying files
        ProgressDialog progressDialog = new ProgressDialog(mainFrame, Translator.get("copy_dialog.copying"));
        AbstractFile destFolder = mainFrame.getActivePanel().getCurrentFolder();
        CopyJob job = new CopyJob(progressDialog, mainFrame, clipboardFiles, destFolder, null, CopyJob.Mode.COPY, FileCollisionDialog.ASK_ACTION);
        progressDialog.start(job);
    }

	@Override
	public ActionDescriptor getDescriptor() {
		return new Descriptor();
	}


    public static final class Descriptor extends AbstractActionDescriptor {
    	public static final String ACTION_ID = "PasteClipboardFiles";

		public String getId() { return ACTION_ID; }

		public ActionCategory getCategory() { return ActionCategory.SELECTION; }

		public KeyStroke getDefaultAltKeyStroke() { return null; }

		public KeyStroke getDefaultKeyStroke() {
            if (OsFamily.getCurrent() != OsFamily.MAC_OS_X) {
                return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
            } else {
                return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK);
            }
        }

        public MuAction createAction(MainFrame mainFrame, Map<String,Object> properties) {
            return new PasteClipboardFilesAction(mainFrame, properties);
        }
    }
}
