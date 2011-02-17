package at.ac.tuwien.ifs.qse.tdd.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ShowMockDialog extends Dialog{

	private String message;
	private String title;

	protected ShowMockDialog(Shell shell, String title, String message) {
		super(shell);
		this.message = message;
		this.title = title;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
		newShell.setSize(500, 400);
	}
	
	protected Control createDialogArea(Composite parent) {
		
		Composite control = (Composite)super.createDialogArea(parent);
		FillLayout layout = new FillLayout();
		control.setLayout(layout);
		
		Text text = new Text(control,SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setText(message);
		
		return control;
	}


}
