%%%%%%%%% Two-Room Blocks World Planner %%%%%%%%%%%%%%%%%%%%%%%%%%
%%%
%%% Based on the single-room planner provided in
%%%
%%% CAP 4630
%%% Artificial Intelligence:
%%%
%%% Richard Snyder and Jimmy Seeber
%%% FALL 2018
%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



:- module( planner,
	   [
	       plan/4,change_state/3,conditions_met/2,member_state/2,
	       move/3,go/2,test1/0,test2/0
	   ]).

:- [utils].

plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
plan(State, Goal, Been_list, Moves) :-
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			plan(Child_state, Goal, New_been_list, New_moves),!.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

% moves

move(pickup(X), [handempty, handroom(Z), clear(X), inroom(X, Z), on(X, Y)], 
				[del(handempty), del(clear(X)), del(on(X, Y)), del(inroom(X, Z)), add(clear(Y)), add(holding(X))]).

move(pickup(X), [handempty, handroom(Z), clear(X), inroom(X, Z), ontable(X)], 
				[del(handempty), del(clear(X)), del(ontable(X)), del(inroom(X, Z)), add(holding(X))]). 

move(putdown(X), [holding(X), handroom(Z)], 
				 [del(holding(X)), add(inroom(X, Z)), add(ontable(X)), add(clear(X)), add(handempty)]). 

move(stack(X, Y), [holding(X), handroom(Z), clear(Y), inroom(Y, Z)], 
				  [del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y)), add(clear(X)), add(inroom(X, Z))]). 

move(goroom1, [handroom(2)], 
			  [del(handroom(2)), add(handroom(1))]). 

move(goroom2, [handroom(1)], 
			  [del(handroom(1)), add(handroom(2))]). 

go(S, G) :- plan(S, G, [S], []). 

test1 :- go([handempty, handroom(1), inroom(a, 1), inroom(b, 1), inroom(c, 1), ontable(b), ontable(c), on(a, b), clear(c), clear(a)],
			[handempty, handroom(1), inroom(a, 1), inroom(b, 1), inroom(c, 1), ontable(c), on(b, c), on(a, b), clear(a)]). 

test2 :- go([handempty, handroom(1), inroom(a, 1), inroom(b, 1), inroom(c, 1), ontable(b), ontable(c), on(a, b), clear(c), clear(a)], 
			[handempty, handroom(1), inroom(a, 2), inroom(b, 2), inroom(c, 2), ontable(b), on(c, b), on(a, c), clear(a)]). 

