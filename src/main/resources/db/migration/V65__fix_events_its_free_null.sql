-- Corrige eventos existentes que têm its_free NULL (criados antes da coluna existir)
UPDATE events SET its_free = true WHERE its_free IS NULL;
