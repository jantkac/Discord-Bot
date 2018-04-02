package vendor.abstracts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import vendor.interfaces.Console;
import vendor.modules.Logger;
import vendor.objects.LoggableJTextArea;

public abstract class AbstractUIConsole extends JFrame implements Console {
	
	public AbstractUIConsole(){
		super();
	}
	
	@Override
	public String getInput(String message){
		return JOptionPane.showInputDialog(this, message);
	}
	
	@Override
	public int getConfirmation(String question, QuestionType questionType){
		// TODO : Implement Confirmation UI (with JOptionPane?)
		
		int jOptionType;
		
		switch(questionType){
		case YES_NO:
			jOptionType = JOptionPane.YES_NO_OPTION;
			break;
		case YES_NO_CANCEL:
		default:
			jOptionType = JOptionPane.YES_NO_CANCEL_OPTION;
			break;
		}
		
		int choice = JOptionPane.showConfirmDialog(this, question,
				"Hey, gotta confirm this!", jOptionType);
		
		return choice;
		
	}
}