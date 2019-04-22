package arguments;

public class ArgumentParsing {


    /**
     * Parses arguments into key value pairs for the optionsContainer, which stores all
     * configurable values in a good readable form
     * Also defaults all given values to lower case to avoid stupid things
     * @param args {@link String[]}
     * @return {@link OptionsContainer}
     */
    public static Options parseStringsFromArgs(String[] args) {

        Options optionsContainer = new Options();
        optionsContainer.setArgs(args);
        
        return optionsContainer;
    }



}
