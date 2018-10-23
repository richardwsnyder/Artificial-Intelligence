% Arithmetic:

% R :- mod(7,2).

len([], 0).
len([_|T], N) :- len(T,X), N is X+1.

len(a,b,c,d,e,[a,b],X).

increment(X, Y) :- Y is X + 1, Y> 0.

increment(6, X).
X = 7.
