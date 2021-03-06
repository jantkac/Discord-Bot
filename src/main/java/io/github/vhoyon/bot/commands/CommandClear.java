package io.github.vhoyon.bot.commands;

import io.github.vhoyon.bot.errorHandling.BotError;
import io.github.vhoyon.bot.utilities.BotCommand;
import io.github.vhoyon.bot.utilities.specifics.CommandConfirmed;
import io.github.vhoyon.vramework.exceptions.BadContentException;
import io.github.vhoyon.vramework.interfaces.Stoppable;
import io.github.vhoyon.vramework.objects.ParametersHelp;
import io.github.vhoyon.vramework.utilities.MessageManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This command clears every messages that matches the request's conditions in
 * the TextChannel where it was called from.
 * 
 * @version 1.0
 * @since v0.4.0
 * @author Stephano
 */
public class CommandClear extends BotCommand implements Stoppable {
	
	protected ArrayList<Predicate<Message>> conditions;
	protected int messageWeight = 0;
	
	protected MessageManager confManager;
	protected MessageManager notifyManager;
	
	protected void addReplacement(String key, Object value){
		
		if(confManager == null){
			confManager = new MessageManager();
			notifyManager = new MessageManager();
		}
		
		confManager.addReplacement(key, value);
		notifyManager.addReplacement(key, value);
		
	}
	
	protected void addCondition(String parameterUsed,
			Predicate<Message> condition){
		
		if(this.conditions == null)
			this.conditions = new ArrayList<>();
		
		this.conditions.add(condition);
		
		onParameterPresent(parameterUsed, param -> {
			
			this.messageWeight += param.getWeight();
			
		});
		
	}
	
	@Override
	public void action(){
		
		boolean shouldDoClear = true;
		
		if(hasParameter("u", "s", "b")){
			
			try{
				
				String paramUsed;
				
				final Member usr;
				
				if(hasParameter("u")){
					usr = getParameterAsMention("u");
					paramUsed = "u";
				}
				else if(hasParameter("s")){
					usr = getMember();
					paramUsed = "s";
				}
				else{
					usr = getBotMember();
					paramUsed = "b";
				}
				
				addReplacement("user", usr.getAsMention());
				
				addCondition(paramUsed,
						message -> usr.equals(message.getMember()));
				
			}
			catch(BadContentException e){
				sendMessage(lang("MentionError", code("@[username]")));
				
				shouldDoClear = false;
			}
			
		}
		
		if(shouldDoClear)
			doClearLogic(hasParameter("i"));
		
	}
	
	protected void setupConfMessages(){
		
		if(this.confManager == null)
			this.confManager = new MessageManager();
		
		this.confManager.addMessage(-4, "ConfBotInv");
		this.confManager.addMessage(-2, "ConfSelfInv", "user");
		this.confManager.addMessage(-1, "ConfUsrInv", "user");
		this.confManager.addMessage(0, "ConfAll");
		this.confManager.addMessage(1, "ConfUsr", "user");
		this.confManager.addMessage(2, "ConfSelf", "user");
		this.confManager.addMessage(4, "ConfBot");
		
	}
	
	protected void setupNotifyMessages(){
		
		if(this.notifyManager == null)
			this.notifyManager = new MessageManager();
		
		this.notifyManager.addMessage(-4, "NotifBotInv");
		this.notifyManager.addMessage(-2, "NotifSelfInv", "user");
		this.notifyManager.addMessage(-1, "NotifUsrInv", "user");
		this.notifyManager.addMessage(0, "NotifAll");
		this.notifyManager.addMessage(1, "NotifUsr", "user");
		this.notifyManager.addMessage(2, "NotifSelf", "user");
		this.notifyManager.addMessage(4, "NotifBot");
		
	}
	
	/**
	 * Executes the logic to clear the messages with the conditions present in
	 * the value of CommandClear's conditions array. This method resolves the
	 * confirmation and notification messages based on the weight of the current
	 * conditions status.
	 * 
	 * @see #doClearLogic(String, String, boolean)
	 */
	protected void doClearLogic(boolean shouldInvert){
		
		if(shouldInvert)
			this.messageWeight *= -1;
		
		setupConfMessages();
		setupNotifyMessages();
		
		String confMessage = confManager.getMessage(this.messageWeight,
				this.getDictionary(), this);
		String notifMessage = notifyManager.getMessage(this.messageWeight,
				this.getDictionary(), this);
		
		doClearLogic(confMessage, notifMessage, shouldInvert);
		
	}
	
	/**
	 * Executes the logic to clear the messages with the conditions present in
	 * the value of CommandClear's conditions array.
	 *
	 * @param confMessage
	 *            The message to send as confirmation if necessary
	 * @param notifyMessage
	 *            The message to send to notify that every message has been
	 *            deleted
	 * @since v0.10.0
	 */
	protected void doClearLogic(final String confMessage,
			final String notifyMessage, boolean shouldInvert){
		
		new CommandConfirmed(this){
			
			@Override
			public String getConfMessage(){
				return confMessage;
			}
			
			@Override
			public void confirmed(){
				
				try{
					
					deleteMessages(CommandClear.this.conditions, shouldInvert);
					
					if(notifyMessage != null && isAlive() && hasParameter("n"))
						sendInfoMessage(notifyMessage);
					
				}
				catch(PermissionException e){
					new BotError(CommandClear.this, lang("NoPermission"));
				}
				
			}
			
		};
		
	}
	
	/**
	 * Deletes all the messages in the TextChannel where this command was called
	 * from and applies the condition supplied by the predicate as a parameter
	 * (view {@code messageCondition}).
	 * <p>
	 * This deletion is done by batch of 100 messages until it can't anymore
	 * (Discord doesn't allow do batch delete messages older than 2 weeks old,
	 * so we batch delete all we can and delete individually the rest).
	 * </p>
	 * 
	 * @param messageConditions
	 *            This parameter is used to run arbitrary code to make
	 *            conditions on the message itself. You could use this to only
	 *            delete the messages of certain users, for example. Can be
	 *            {@code null} to not have any condition and delete all
	 *            messages.
	 * @throws PermissionException
	 *             Thrown if the bot does not have the sufficient permissions to
	 *             delete a message in the list.
	 * @since v0.10.0
	 */
	protected void deleteMessages(
			final ArrayList<Predicate<Message>> messageConditions,
			boolean shouldInvert) throws PermissionException{
		
		MessageHistory messageHistory = getTextContext().getHistory();
		
		boolean shouldCompleteBeforeNext = hasParameter("w");
		
		final List<Message> fullMessageHistory = this.getFullMessageList(
				messageHistory, messageConditions, shouldInvert);
		
		boolean deletingIndividually = fullMessageHistory.size() < 2;
		
		final int batchSize = 100;
		
		for(int i = 0; i < fullMessageHistory.size() && isAlive(); i += batchSize){
			
			List<Message> currentMessageList;
			
			try{
				currentMessageList = fullMessageHistory.subList(i, i
						+ batchSize);
			}
			catch(IndexOutOfBoundsException e){
				currentMessageList = fullMessageHistory.subList(i,
						fullMessageHistory.size());
			}
			
			if(!deletingIndividually){
				
				try{
					
					if(shouldCompleteBeforeNext){
						getTextContext().deleteMessages(currentMessageList)
								.complete();
					}
					else{
						getTextContext().deleteMessages(currentMessageList)
								.queue();
					}
					
				}
				catch(IllegalArgumentException e){
					deletingIndividually = true;
				}
				
			}
			
			if(deletingIndividually){
				
				for(Message message : currentMessageList){
					
					if(shouldCompleteBeforeNext){
						message.delete().complete();
					}
					else{
						message.delete().queue();
					}
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Gets the full message list from the {@code messageHistory} parameter.
	 * 
	 * @param messageHistory
	 *            The {@link MessageHistory} object used by this TextChannel to
	 *            reference which history should be searched for.
	 * @return A {@link List} of of all {@link Message} that is present in the
	 *         TextChannel where this command was invoked.
	 * @since v0.10.0
	 * @see #getFullMessageList(MessageHistory, Predicate, boolean)
	 */
	protected List<Message> getFullMessageList(MessageHistory messageHistory,
			boolean shouldInvert){
		return this.getFullMessageList(messageHistory,
				(ArrayList<Predicate<Message>>)null, shouldInvert);
	}
	
	/**
	 * Gets the full message list that matches the condition supplied by the
	 * {@code messageCondition} parameter out of the {@code messageHistory}
	 * parameter.
	 *
	 * @param messageHistory
	 *            The {@link MessageHistory} object used by this TextChannel to
	 *            reference which history should be searched for.
	 * @param messageCondition
	 *            This parameter is used to run arbitrary code to make a
	 *            condition on the message itself. You could use this to only
	 *            find the messages of certain users, for example. Can be
	 *            {@code null} (or see
	 *            {@link #getFullMessageList(MessageHistory, boolean)}) to not
	 *            have any condition and find all messages.
	 * @return A {@link List} of of all {@link Message} that is present in the
	 *         TextChannel where this command was invoked.
	 * @since v0.10.0
	 * @see #getFullMessageList(MessageHistory, boolean)
	 * @see #getFullMessageList(MessageHistory, ArrayList, boolean)
	 */
	protected List<Message> getFullMessageList(MessageHistory messageHistory,
			Predicate<Message> messageCondition, boolean shouldInvert){
		
		ArrayList<Predicate<Message>> singleCondition = new ArrayList<>();
		singleCondition.add(messageCondition);
		
		return getFullMessageList(messageHistory, singleCondition, shouldInvert);
		
	}
	
	/**
	 * Gets the full message list that matches the conditions supplied by the
	 * {@code messageCondition} parameter out of the {@code messageHistory}
	 * parameter.
	 *
	 * @param messageHistory
	 *            The {@link MessageHistory} object used by this TextChannel to
	 *            reference which history should be searched for.
	 * @param messageConditions
	 *            This parameter is used to run arbitrary code to make
	 *            conditions on the message itself. You could use this to only
	 *            find the messages of certain users, for example. Can be
	 *            {@code null} (or see
	 *            {@link #getFullMessageList(MessageHistory, boolean)}) to not
	 *            have any condition and find all messages.
	 * @return A {@link List} of of all {@link Message} that is present in the
	 *         TextChannel where this command was invoked with the conditions
	 *         applied.
	 * @since v0.10.0
	 * @see #getFullMessageList(MessageHistory, boolean)
	 * @see #getFullMessageList(MessageHistory, Predicate, boolean)
	 */
	protected List<Message> getFullMessageList(MessageHistory messageHistory,
			ArrayList<Predicate<Message>> messageConditions,
			boolean shouldInvert){
		
		boolean isEmpty;
		
		do{
			
			isEmpty = messageHistory.retrievePast(100).complete().isEmpty();
			
		}while(!isEmpty && isAlive());
		
		List<Message> fullHistory = messageHistory.getRetrievedHistory();
		
		if(messageConditions == null)
			return fullHistory;
		
		ArrayList<Message> messagesWithCondition = new ArrayList<>();
		
		boolean conditionsGateIsAnd = !hasParameter("or");
		
		//@formatter:off
		fullHistory.forEach(message -> {
			
			boolean shouldDelete = conditionsGateIsAnd;
			
			// Acts as a AND gate, so if any of the conditions is false, the message is not deleted
			for(Predicate<Message> messageCondition : messageConditions){
				
				// Determine Gate logic and invert the logic if it's an OR
				if(messageCondition.test(message) != conditionsGateIsAnd){
					shouldDelete = !conditionsGateIsAnd;
					
					break;
				}
				
			}
			
			// If inverting, the condition must be false to clear this message
			if(shouldDelete != shouldInvert)
				messagesWithCondition.add(message);
			
		});
		//@formatter:on
		
		return messagesWithCondition;
		
	}
	
	@Override
	public Object getCalls(){
		return CLEAR;
	}
	
	@Override
	public String getCommandDescription(){
		return "Clear all the messages that in the text channel you execute the command!";
	}
	
	@Override
	public ParametersHelp[] getParametersDescriptions(){
		return new ParametersHelp[]
		{
			new ParametersHelp(
					"Allows you to delete the messages of a user you specify.",
					1, "u", "user"),
			new ParametersHelp("Allows you to delete your own messages.",
					false, 2, "s", "self"),
			new ParametersHelp(
					"Allows you to delete all of the bots messages.", false, 3,
					"b", "bot"),
			new ParametersHelp(
					"Inverts the condition applied to the command (example : using this in combination with "
							+ formatParameter("s")
							+ " would clear messages of everyone but yourself).",
					false, "i", "invert"),
			new ParametersHelp(
					"This makes the bot notify the text channel of what it cleared.",
					false, "n", "notify"),
			new ParametersHelp(
					"Waits that the message has been successfully deleted before deleting the others. Useful if you are not sure if you should delete all the messages as you can stop the command.",
					false, "w", "wait"),
			new ParametersHelp(
					"Allows the conditions parser to use an OR logic gate instead of an AND for all conditions.",
					false, "or"),
		};
	}
	
}
