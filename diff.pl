

:- module(diff, [
                member/2,
                member_set/2,
                set_diff/3
                ]).

member(X, [X|_]).
member(X, [_|T]) :- member(X, T).

member_set(E,S) :- member(E,S).

set_diff([], _, []).
set_diff([H|T], S, T_new) :-
        member_set(H, S),
        set_diff(T, S, T_new), !.

set_diff([H|T], S, [H|T_new]) :-
        set_diff(T, S, T_new).
