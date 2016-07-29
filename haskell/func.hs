-- | learnyouahaskell.com
-- | haskell tutorials note
-- | Tsinghua University
-- | MENG YUE

-- | Basic 
-- | 1. run 'ghci' to start the command
-- | 2. in ghci use ':l func' to load the func.hs file for describing functions
-- | 3. in ghci use ':set prompt XXX' to set the left sign to XXX
-- | 4. in ghci use :t for looking up the types of the parameters for function




-- | 'if' expression must have a return value and must have an "else"
doubleSmallNumber' x= (if x>100 then x else x*2)+1

-- |  expression match: from up to down
lucky :: (Integral a) => a -> String
lucky 7 = "LUCKY NUMBER SEVEN"
lucky x = "Sorry, you're out of luck, pal!"

-- | the same like the first one
sayMe :: (Integral a ) => a -> String
sayMe 1 = "One!"
sayMe 2 = "Two!"
sayMe 3 = "Three!"
sayMe 4 = "Four!"
sayMe 5 = "Five!"
sayMe x = "Not between 1 and 5"

-- | recursion function 
factorial :: (Integral a) => a -> a
factorial 0 = 1
factorial n = n * factorial (n - 1)

-- | remember to write catch for all the consideration
charName :: Char -> String
charName 'a' = "Albert"
charName 'b' = "Broseph"
charName 'c' = "Cecil"
charName  x  = "CATCH-ALL NAME"

-- | patterns matching that used on tuples' form
addVectors :: (Num a) => (a, a) -> (a, a) -> (a, a)
addVectors (x1, y1) (x2, y2) = (x1 + x2, y1 + y2)

-- | using _ to replace which things we don't care
first :: (a, b, c) -> a
first (x, _, _) = x

second :: (a, b, c) -> b
second (_, y, _) = y

third :: (a, b, c) -> c
third (_, _, z) = z

-- | pattern match can also be used in list comprehensions.
findSumInEveryPairs ::  [(Integer,Integer)] -> [Integer]
findSumInEveryPairs xs = [x+y | (x,y) <- xs] 

-- | using ':' to seperate items we want to illustrate
head' :: [a] -> a
head' [] = error "Can't use empty list, your sb!"
head' (x:_) = x

-- | using recursion and match patterns
length' :: [a] -> Integer
length' [] = 0
length' (_:xs) = 1 + length' xs

-- | gurads: a form of statement control expression more readable than if-else
bmiTell :: (RealFloat a) => a -> String
bmiTell bmi
	| bmi <= 18.5 = "You're underweight."
	| bmi <= 25.0 = "You're supposedly normal."
	| bmi <= 30.0 = "You're fat! Lose some weight, fatty!"
	| otherwise = "You're a whale, cong!"

-- | using where expression for clause description
-- | REMEMBER lines below 'where' should head alligned no matter space or tab
bmiTell' :: (RealFloat a) => a -> a -> String
bmiTell' weight height
	| bmi <= 18.5 = "thin"
	| bmi <= 25.0 = "kind of thin"
	| bmi <= fat = "kind of fat"
	| otherwise  = "fat"
	where	bmi = weight / height ^ 2
		skinny = 18.5
		normal = 25.0
		fat =30.0

-- | REMEMBER only [Char] can be String, so cannot f ++ "xxx", but [f] ++ "xxx"
initials :: String -> String -> String
initials firstname lastname = [f] ++ ". " ++ [l] ++ "."
	where	(f:_) = firstname
		(l:_) = lastname

-- | define functions 'bmi' to calculate bmi
calcBmis :: (RealFloat a) => [(a,a)] -> [a]
calcBmis xs =[bmi w h | (w,h) <- xs]
	where bmi weight height = weight / height ^2

-- | let <bindings> in <expression>. unlike 'where bindings' is a syntactic constructs, 'let bindings' is  an expression.
cylinder :: (RealFloat a) => a -> a -> a
cylinder r h =
	let	sideArea = 2 * pi * r * h
		topArea = pi * r ^ 2
	in 	sideArea + 2 * topArea
