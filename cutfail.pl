:- module( negation,
          [enjoys/2, burger/1, big_mac/1,
          big_kahuna_burger/1, whopper/1]).

% cut-fail condition occurs first, then success condition follows.
% enjoys(vincent, X) :- big_kahuna_burger(X), !, fail.

% this code allows the general case to be used. How to show all?
enjoys(vincent, X) :- burger(X), not(big_kahuna_burger(X)).
enjoys(vincent, X) :- burger(X).

burger(X) :- big_mac(X).
burger(X) :- big_kahuna_burger(X).
burger(X) :- whopper(X).

big_mac(a).
big_mac(b).
big_kahuna_burger(c).
whopper(d).
