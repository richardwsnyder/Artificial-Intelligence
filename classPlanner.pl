
% Planning Concepts:
% plan is a sequence of actions for achieving a goal.
% actions have antecedents
    % (preconditions):
        % what the current state of the world must have for an action to be available.

    % actions have consequences (effects):
        % the state of the world changes based on the action taken.
        % actions typically leave most aspects of the world the same.

% Assumptions made by Prolog:
  % closed-world - everything that is true in the world is stated in the KB
  % unique names - if we have 2 different names => 2 different objects.

% Prolog rules can easily associate preconditions and effects with particular actions.

% Block World:
  % Problem domain - Set of cube-shaped blocks on a table; some stacked.
  % Goal - Rearrange blocks into a particular arrangement.

% State predicates:
  % robot arm - handempty
  %             holding(X)
  % blocks - ontable(X)
  %          on(X, Y)            block X is on top of block Y
  %          clear(X)            nothing on top of block X

% Rules:
  % for actions, of the form: action( name(), [preconditions], [effects]).
  % for verifying Preconditions
  % for changing state based on action effects.

% Dissecting a Prolog blocks World Planner:

:- module( planner,
            [plan/4, change_state/3, conditions_met/2, member_set/2,
            move/3, go/2, test/0, test2/0]).

:- [utils].

plan(State, Goal, _, Moves) :- equal_set(State, Goal),                            % terminate when current state is goal state
                              write('moves are'), nl,
                              reverse_print_stack(Moves).

plan(State, Goal, Been_list, Moves) :-                                            % Been_list is a visited array to see if we have been to a Child_state before
                                  move(Name, Preconditions, Actions),
                                  conditions_met(Preconditions, State),
                                  change_state(State, Actions, Child_state),
                                  not(member_state(Child_state, Been_list)),
                                  stack(Child_state, Been_list, New_been_list),
                                  stack(Name, Moves, New_moves),
                            plan(Child_state, Goal, New_been_list, New_moves), !.
                            % note: plan is recursively defined ^


% Administrative Rules:
change_state(S, [], S).
                                                                                  % changing the state based on taking an action.
change_state(S, [add(P)|T], S_new) :- change_state(S, T, S2),
                                      add_to_set(P, S2, S_new), !.

change_state(S, [del(P)|T], S_new) :- change_state(S, T, S2),
                                      remove_from_set(P, S2, S_new), !.

conditions_met(P, S) :- subset(P, S).                                             % determining when preconditions are satisfied.
                                                                                  % determining whether a state is in the "Been_list"
member_state(S, [H|_]) :- equal_set(S, H).
member_state(S, [_|T]) :- member_state(S, T).


% Planner Actions:
  % Action predicates determine the state changes for each possible situation
  % They also provide a move "name" for use in recording the move.
/* move types */
move(pickup(X), [handempty, clear(X), on(X, Y)],
                [del(handempty), del(clear(X)), del(on(X, Y)),
                                add(clear(Y)), add(holding(X))]).

move(pickup(X), [handempty, clear(X), ontable(X)],
                [del(handempty), del(clear(X)), del(ontable(X)),
                                add(holding(X)) ]).

move(putdown(X), [holding(X)],
                [del(holding(X)), add(ontable(X)), add(clear(X)),
                                  add(handempty)]).

move(stack(X, Y), [holding(X), clear(Y)],
                  [del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y)),
                                    add(clear(X))]).

/* run commands */
% Planner: Launching and Testing:
go(S, G) :- plans(S, G, [S], []).

test :- go([handempty, ontable(b), ontable(c), on(a, b), clear(c), clear(a)],
            [handempty, ontable(c), on(a, b), on(b, c), clear(a) ]).

test2 :- go([handempty, ontable(b), ontable(c), on(a, b), clear(c), clear(a)],
            [handempty, ontable(a), ontable(b), on(c, b), clear(a), clear(c) ]).
