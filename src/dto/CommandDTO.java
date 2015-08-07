package dto;

import java.util.ArrayList;

public class CommandDTO {
	private ArrayList<ACTION> commands;
	
	public CommandDTO(){}
	public CommandDTO(ArrayList<ACTION> coma){
		this.commands = coma;
	}
	
	public ArrayList<ACTION> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<ACTION> commands) {
		this.commands = commands;
	}
	public void addCommand(ACTION newCommand){
		this.commands.add(newCommand);
	}
}
