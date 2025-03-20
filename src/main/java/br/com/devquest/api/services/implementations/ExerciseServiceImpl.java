package br.com.devquest.api.services.implementations;

import br.com.devquest.api.enums.Difficulty;
import br.com.devquest.api.enums.Technology;
import br.com.devquest.api.model.dtos.ExerciseDTO;
import br.com.devquest.api.model.entities.Exercise;
import br.com.devquest.api.model.entities.User;
import br.com.devquest.api.repositories.ExerciseRepository;
import br.com.devquest.api.repositories.UserRepository;
import br.com.devquest.api.services.IExerciseService;
import br.com.devquest.api.services.generators.ExerciseGenerator;
import br.com.devquest.api.utils.TokenJWTDecoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import static br.com.devquest.api.mappers.DozerMapper.*;

@Service
public class ExerciseServiceImpl implements IExerciseService {

  private ExerciseRepository repository;
  private TokenJWTDecoder tokenJWTDecoder;
  private UserRepository userRepository;
  private ExerciseGenerator exerciseGenerator;

  public ExerciseServiceImpl(ExerciseRepository repository,
                             TokenJWTDecoder tokenJWTDecoder,
                             UserRepository userRepository,
                             ExerciseGenerator exerciseGenerator) {

    this.repository = repository;
    this.tokenJWTDecoder = tokenJWTDecoder;
    this.userRepository = userRepository;
    this.exerciseGenerator = exerciseGenerator;
  }

  @Transactional
  @Override
  public ExerciseDTO generateExercise(String token, Technology technology, Difficulty difficulty) {
    User user = userRepository.findByUsername(tokenJWTDecoder.getUsernameByToken(token));
    Exercise exercise;
    List<Exercise> exercisesWithSameTechnologyAndDifficulty = repository
            .findByTechnologyAndDifficulty(technology, difficulty);

    if (!exercisesWithSameTechnologyAndDifficulty.isEmpty()) {
      exercise = searchForExerciseNotAnsweredByUser(exercisesWithSameTechnologyAndDifficulty, user);
      if (exercise != null) return parseObject(exercise, ExerciseDTO.class);
    }

    exercise = exerciseGenerator.createAndSave(technology, difficulty);
    return parseObject(exercise, ExerciseDTO.class);
  }

  private Exercise searchForExerciseNotAnsweredByUser(List<Exercise> exercises, User user) {
    return exercises.stream()
            .filter(e -> repository.exerciseWasNotAnsweredByUser(e.getId(), user.getId()))
            .findFirst()
            .orElse(null);
  }

}
