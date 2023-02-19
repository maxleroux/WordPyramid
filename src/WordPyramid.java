import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordPyramid {
    // lists representing the words as they are represented in the pyramid
    // '_' means no letter has been placed in that spot yet
    static ArrayList<String> oneLetterWord = new ArrayList<>(Arrays.asList("_"));
    static ArrayList<String> twoLetterWord = new ArrayList<>(Arrays.asList("_", "_"));
    static ArrayList<String> threeLetterWord = new ArrayList<>(Arrays.asList("_", "_", "_"));
    static ArrayList<String> fourLetterWord = new ArrayList<>(Arrays.asList("_", "_", "_", "_"));
    static ArrayList<String> fiveLetterWord = new ArrayList<>(Arrays.asList("_", "_", "_", "_", "_"));
    static List<ArrayList<String>> pyramid = new ArrayList<>(Arrays.asList(oneLetterWord, twoLetterWord,
            threeLetterWord, fourLetterWord, fiveLetterWord));
    // store all the words and the number of total possible words at each length in the pyramid
    static List<String> wordDictionary = new ArrayList<>();
    static List<Integer> wordsPerLength = new ArrayList<>();
    // used for user input
    static Scanner scanner;
    // represents if the game is finished and if the pyramid is in a completed state
    static boolean gameOver = false;
    static boolean successfulPyramid = false;

    public static void main(String[] args) {
        importWords();
        scanner = new Scanner(System.in);
        playGame();
    }

    // ask the player for the first letter they are given and loop with new letters until completion
    private static void playGame() {
        System.out.println("First letter:");
        while (gameOver == false && successfulPyramid == false) {
            System.out.println(wordsPerLength);
            determinePosition(scanner.nextLine());
            printPyramid();
            isPyramidComplete();
            System.out.println("Next letter:");
        }
        endGame();
    }

    // determine where the player should place their letter
    private static void determinePosition(String nextLine) {
        List<ArrayList<String>> possibleWords = getAllPossibleWords(nextLine);
        possibleWords = filterCompletedLengthWords(possibleWords);
        List<ArrayList<Integer>> numWordsPerPosition = getNumWordsPerPosition(possibleWords, nextLine);
        placeLetter(numWordsPerPosition, nextLine);
    }

    // create the list of all words that contain the given letter
    private static List<ArrayList<String>> getAllPossibleWords(String nextLine) {
        // create a list of lists for each length of word
        List<ArrayList<String>> listOfPossibleWords = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listOfPossibleWords.add(new ArrayList<>());
        }

        // for every word that contains the letter, add it to the corresponding list
        for (String word : wordDictionary) {
            if (word.contains(nextLine)) {
                listOfPossibleWords.get(word.length() - 1).add(word);
            }
        }

        return listOfPossibleWords;
    }

    // if the tier of the pyramid is already completed, remove words of that length from the list
    private static List<ArrayList<String>> filterCompletedLengthWords(List<ArrayList<String>> possibleWords) {
        for (int i = 0; i < pyramid.size(); i++) {
            if (!pyramid.get(i).contains("_")) {
                possibleWords.get(i).removeAll(possibleWords.get(i));
            }
        }
        return possibleWords;
    }

    // determine how many words can be created with the letter placed at each position
    private static List<ArrayList<Integer>> getNumWordsPerPosition(List<ArrayList<String>> possibleWords, String nextLine) {
        // create a list of lists that represent the number of words that contain the letter at each position
        // ex: [[0], [1, 2]] would show that the letter 'h' makes 0 1-letter words, and makes 3 2-letter words,
        // 1 with it being the first character and 2 with it being the second
        List<ArrayList<Integer>> numWordsPerPosition = new ArrayList<>();
        for (int i = 1; i< 6; i++) {
            numWordsPerPosition.add(new ArrayList<>());
            for (int j = 1; j <= i; j++) {
                numWordsPerPosition.get(i-1).add(0);
            }
        }

        for (int i = 0; i < pyramid.size(); i++) {
            ArrayList<String> words = possibleWords.get(i);
            if (pyramid.get(i).stream().filter(item -> !item.equals("_")).count() > 0) {
                numWordsPerPosition = getNumWordsPerPositionHelper(i, nextLine, numWordsPerPosition, words);
            } else {
                for (String word : words) {
                    numWordsPerPosition.get(i).set(word.indexOf(nextLine),
                            numWordsPerPosition.get(i).get(word.indexOf(nextLine)) + 1);
                }
            }
        }
        return numWordsPerPosition;
    }

    // iterate through each word and validate that it matches the restrictions of the current pyramid state
    private static List<ArrayList<Integer>> getNumWordsPerPositionHelper(int i, String nextLine, List<ArrayList<Integer>> numWordsPerPosition,
                                                                         ArrayList<String> words) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int j = 0; j < pyramid.get(i).size(); j++) {
            if (!pyramid.get(i).get(j).equals("_")) {
                map.put(pyramid.get(i).get(j), j);
            }
        }

        for (String word : words) {
            boolean validWord = true;
            for (Map.Entry<String, Integer> set : map.entrySet()) {
                validWord = validWord && (word.substring(set.getValue(), set.getValue() + 1).equals(set.getKey().toLowerCase()))
                        && (pyramid.get(i).get(word.indexOf(nextLine)).equals("_"));
            }
            if (validWord) {
                numWordsPerPosition.get(i).set(word.indexOf(nextLine), numWordsPerPosition.get(i).get(word.indexOf(nextLine)) + 1);
            }
        }

        return numWordsPerPosition;
    }

    // calculate which position creates the most words and place the letter there
    private static void placeLetter(List<ArrayList<Integer>> numWordsPerPosition, String nextLine) {
        List<Double> percentageOfTotalWords = new ArrayList<>();
        List<Integer> charPosns = new ArrayList<>();
        for (int i = 0; i < numWordsPerPosition.size(); i++) {
            int maxWordsCreated = Collections.max(numWordsPerPosition.get(i));
            double percentage = ((double) maxWordsCreated) / wordsPerLength.get(i);
            percentageOfTotalWords.add(percentage);
            charPosns.add(numWordsPerPosition.get(i).indexOf(Collections.max(numWordsPerPosition.get(i))));
        }
        for (int i = 0; i < percentageOfTotalWords.size(); i++) {
            // allows for the weight of the percentages to be altered to focus on longer/shorter words
            percentageOfTotalWords.set(i, percentageOfTotalWords.get(i)*Math.pow(1, i));
        }
        if (Collections.max(percentageOfTotalWords) > 0) {
            int lengthOfWord = percentageOfTotalWords.indexOf(Collections.max(percentageOfTotalWords));
            int position = charPosns.get(lengthOfWord);
            System.out.println("Place '" + nextLine.toUpperCase() + "' in the " + (lengthOfWord + 1) + "-letter-word in spot " + (position + 1));
            pyramid.get(lengthOfWord).set(position, nextLine.toUpperCase());
            updateDictionary(nextLine, lengthOfWord + 1, position);
        } else {
            System.out.println("SKIP");
        }
    }

    // remove all words from the dictionary that would no longer work
    private static void updateDictionary(String nextLine, int lengthOfWord, int position) {
        wordsPerLength.removeAll(wordsPerLength);
        List<String> wordsToRemove = new ArrayList<>();
        for (String word : wordDictionary) {
            if (word.length() == lengthOfWord && !word.substring(position, position + 1).equals(nextLine)) {
                wordsToRemove.add(word);
            }
        }

        for (String word : wordsToRemove) {
            wordDictionary.remove(word);
        }

        int numOneLetterWords = 0;
        int numTwoLetterWords = 0;
        int numThreeLetterWords = 0;
        int numFourLetterWords = 0;
        int numFiveLetterWords = 0;
        for (String data : wordDictionary) {
            if (data.length() == 1) {
                numOneLetterWords += 1;
            } else if (data.length() == 2) {
                numTwoLetterWords += 1;
            } else if (data.length() == 3) {
                numThreeLetterWords += 1;
            } else if (data.length() == 4) {
                numFourLetterWords += 1;
            } else if (data.length() == 5) {
                numFiveLetterWords += 1;
            }
        }
        wordsPerLength.add(numOneLetterWords);
        wordsPerLength.add(numTwoLetterWords);
        wordsPerLength.add(numThreeLetterWords);
        wordsPerLength.add(numFourLetterWords);
        wordsPerLength.add(numFiveLetterWords);
    }

    // sets the successfulPyramid value to true if the pyramid has no more blank spaces
    private static void isPyramidComplete() {
        boolean completed = true;
        for (int i = 0; i < pyramid.size(); i ++) {
            for (int j = 0; j < pyramid.get(i).size(); j++)
            completed = completed && !pyramid.get(i).get(j).equals("_");
        }
        successfulPyramid = completed;
    }

    // display whether the game was won or not
    private static void endGame() {
        if (successfulPyramid) {
            System.out.println("Yay! We completed the pyramid!");
        } else {
            System.out.println("Damn, maybe next time.");
        }
        scanner.close();
    }

    // display the current state of the word pyramid
    private static void printPyramid() {
        System.out.println(oneLetterWord.get(0));
        System.out.println(twoLetterWord.get(0) + twoLetterWord.get(1));
        System.out.println(threeLetterWord.get(0) + threeLetterWord.get(1) + threeLetterWord.get(2));
        System.out.println(fourLetterWord.get(0) + fourLetterWord.get(1) + fourLetterWord.get(2) + fourLetterWord.get(3));
        System.out.println(fiveLetterWord.get(0) + fiveLetterWord.get(1) + fiveLetterWord.get(2) + fiveLetterWord.get(3) + fiveLetterWord.get(4));
    }

    // import the stored txt file with the list of words, store them in a list, and count
    // the number of words at each length
    private static void importWords() {
        int numOneLetterWords = 0;
        int numTwoLetterWords = 0;
        int numThreeLetterWords = 0;
        int numFourLetterWords = 0;
        int numFiveLetterWords = 0;
        try {
            File wordsFile = new File("words.txt");
            Scanner myReader = new Scanner(wordsFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine().toLowerCase();
                if (data.length() < 6 && !wordDictionary.contains(data) && data.matches("[a-z]+")) {
                    wordDictionary.add(data);
                    if (data.length() == 1) {
                        numOneLetterWords += 1;
                    } else if (data.length() == 2) {
                        numTwoLetterWords += 1;
                    } else if (data.length() == 3) {
                        numThreeLetterWords += 1;
                    } else if (data.length() == 4) {
                        numFourLetterWords += 1;
                    } else if (data.length() == 5) {
                        numFiveLetterWords += 1;
                    }
                }
            }
            wordsPerLength.add(numOneLetterWords);
            wordsPerLength.add(numTwoLetterWords);
            wordsPerLength.add(numThreeLetterWords);
            wordsPerLength.add(numFourLetterWords);
            wordsPerLength.add(numFiveLetterWords);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }
}