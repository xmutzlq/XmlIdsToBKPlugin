package kingplugin.popup.actions;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.xml.sax.SAXException;

import kingplugin.popup.util.XMLManager;

public class GenerateXML implements IObjectActionDelegate {

	private Shell shell;
	private ISelection _selection;

	/**
	 * Constructor for Action1.
	 */
	public GenerateXML() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		boolean shown = false;
		String error = "";
		if (_selection instanceof IStructuredSelection) {

			for (Iterator<?> it = ((IStructuredSelection) _selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IFile file = null;
				if (element instanceof IFile) {
					file = (IFile) element;
				}
				if (file != null) {
					printFileInformation(file);
					try {
						String declarations = parseAndGetSourceFromFileXML(file);
						Clipboard clipBoard = new Clipboard(shell.getDisplay());
						TextTransfer textTransfer = TextTransfer.getInstance();
						org.eclipse.swt.dnd.Transfer[] transfers = new Transfer[] { textTransfer };
						Object[] data = new Object[] { declarations };
						clipBoard.setContents(data, transfers);
						clipBoard.dispose();
						shown = true;
					} catch (Exception e) {
						error = e.getMessage();
						e.printStackTrace();
					}
				}
			}
		}

		if (!shown) {
			MessageDialog.openInformation(shell, "GenerateXML", "Generate Activity was failed: " + error);
		}
	}

	private String parseAndGetSourceFromFileXML(IFile file)
			throws ParserConfigurationException, SAXException, IOException, CoreException {
		XMLManager xmlManager = new XMLManager();
		xmlManager.parseFileXML(file);
		return xmlManager.getSource();
	}

	private void printFileInformation(IFile file) {
		System.out.println("File full path: " + file.getFullPath());
		System.out.println("File name: " + file.getName());
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
	}

}
