package com.ProjectManager.repository;

import com.ProjectManager.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamRepository {
    private static TeamRepository instance;
    private List<Team> teams;

    private TeamRepository() {
        this.teams = new ArrayList<>();
    }

    public Team findByName(String name) {
    for (Team team : teams) {
        if (team.getNome().equalsIgnoreCase(name)) {
            return team;
        }
    }
    return null;
}

    public static TeamRepository getInstance() {
        if (instance == null) {
            instance = new TeamRepository();
        }
        return instance;
    }

    public void save(Team team) {
        teams.removeIf(t -> t.getId().equals(team.getId()));
        teams.add(team);
    }

    public Optional<Team> findById(String id) {
        return teams.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public List<Team> findAll() {
        return new ArrayList<>(teams);
    }

    public List<Team> findByMemberId(String userId) {
        return teams.stream().filter(t -> t.getMemberIds().contains(userId)).collect(Collectors.toList());
    }
}
