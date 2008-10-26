package ambit2.db.processors;

public interface ICommand {
	enum COMMAND {
		START,
		STOP
	}
	COMMAND getCommand();
	void setCommand(COMMAND command);
}
