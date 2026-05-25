import TaskItem from "./TaskItem";

function TaskList({ tasks, onDelete, onUpdate }) {
  return (
    <ul>
      {tasks.map(task => (
        <TaskItem
          key={task.id}
          task={task}
          onDelete={onDelete}
          onUpdate={onUpdate}
        />
      ))}
    </ul>
  );
}

export default TaskList;