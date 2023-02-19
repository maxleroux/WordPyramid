# WordPyramid

Representation of the currently popular "Word Pyramid" TikTok filter, in which letters are randomly generated and have to be placed in a pyramid shape to create 5 words of lengths 1-5. The goal is to create an algorithm that searches through a cumulative list of words to determine the best location to place each letter as they are given.

On compilation, the program will request the user to input the letter that is to be placed in the pyramid. It will then determine which position of which word is the best spot to place the letter and instruct the user to do so. If there are no possible words that can be made with the letter given what is currently in the pyramid, it will instruct the user to skip. After a placement or skip instruction is given, the next letter will be requested and the cycle will continue until the pyramid is completed.
