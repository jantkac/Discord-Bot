package io.github.vhoyon.bot.commands;

import io.github.vhoyon.bot.utilities.abstracts.MusicCommands;
import io.github.vhoyon.bot.utilities.music.MusicManager;
import io.github.vhoyon.bot.errorHandling.BotError;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Command that sends a message containing a list of the tracks that are
 * currently in the playlist of the MusicPlayer of the VoiceChannel that is
 * currently connected to.
 * 
 * @version 1.0
 * @since v0.5.0
 * @author V-ed (Guillaume Marcoux)
 */
public class CommandMusicList extends MusicCommands {
	
	@Override
	public void action(){
		
		if(!isPlaying()){
			new BotError(this, lang("NoList", buildVCommand(MUSIC_PLAY
					+ " [music]")));
		}
		else{
			
			StringBuilder sb = new StringBuilder();
			
			AudioTrack currentTrack = MusicManager.get().getPlayer(this)
					.getAudioPlayer().getPlayingTrack();
			
			sb.append(lang("CurrentTrack", code(currentTrack.getInfo().title)));
			
			if(MusicManager.get().getPlayer(this).getListener().getTrackSize() != 0){
				
				sb.append("\n\n").append(lang("Header")).append("\n\n");
				
				int i = 1;
				
				for(AudioTrack track : MusicManager.get().getPlayer(this)
						.getListener().getTracks()){
					
					sb.append(
							lang("TrackInfo", code(i++),
									code(track.getInfo().title))).append("\n");
					
				}
				
			}
			
			sendMessage(sb.toString());
			
		}
		
	}
	
	@Override
	public Object getCalls(){
		return MUSIC_LIST;
	}
	
	@Override
	public String getCommandDescription(){
		return "Display a list of all the music that you have in the music list";
	}
	
}
